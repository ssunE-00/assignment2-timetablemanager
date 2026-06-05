package edu.flinders.timetable.service;

import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.model.ClassGroup;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.Timetable;
import edu.flinders.timetable.model.TimetableSettings;
import edu.flinders.timetable.result.PendingSwapResult;
import edu.flinders.timetable.result.ScheduleWarning;
import edu.flinders.timetable.result.TimetableGenerationResult;
import edu.flinders.timetable.result.ValidationResult;
import edu.flinders.timetable.util.TextUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TimetableService {
    private final DataRepository repository;
    private final ScheduleService scheduleService;
    private final PreferenceScorer preferenceScorer = new PreferenceScorer();
    private TimetableSettings lastSettings = new TimetableSettings();

    public TimetableService(DataRepository repository, ScheduleService scheduleService) {
        this.repository = repository;
        this.scheduleService = scheduleService;
    }

    public TimetableGenerationResult generateTimetable(TimetableSettings settings) {
        ValidationResult validation = validateSettings(settings);
        if (!validation.isValid()) {
            return TimetableGenerationResult.failure(String.join(" ", validation.getErrors()));
        }

        TimetableSettings safeSettings = settings.copy();
        safeSettings.setTimetableName(resolveTimetableName(safeSettings.getTimetableName()));
        List<ClassRecord> filteredRecords = filterRecords(repository.listClassRecords(), safeSettings);
        if (filteredRecords.isEmpty()) {
            return TimetableGenerationResult.failure("No imported classes matched the selected settings.");
        }

        Map<String, List<ClassGroup>> candidatesByChoice = buildCandidatesByChoice(filteredRecords);
        List<ClassRecord> selectedRecords = new ArrayList<>();

        for (Map.Entry<String, List<ClassGroup>> entry : candidatesByChoice.entrySet()) {
            ClassGroup selectedGroup = chooseBestGroup(entry.getValue(), selectedRecords, safeSettings);
            if (selectedGroup == null) {
                return TimetableGenerationResult.failure("Could not select a compatible class for " + entry.getKey() + ".");
            }
            selectedRecords.addAll(selectedGroup.getRecords());
        }

        Timetable timetable = new Timetable(safeSettings.getTimetableName(), selectedRecords);
        List<ScheduleWarning> warnings = scheduleService.findWarnings(timetable.getRecords(), safeSettings.isAllowLectureOverlap());
        repository.saveTimetable(timetable);
        lastSettings = safeSettings.copy();
        return TimetableGenerationResult.success(timetable, warnings);
    }

    public ValidationResult validateSettings(TimetableSettings settings) {
        ValidationResult result = ValidationResult.valid();
        if (settings == null) {
            result.addError("Timetable settings are required.");
            return result;
        }
        if (settings.getTopicCodes() == null || settings.getTopicCodes().isEmpty()) {
            result.addError("At least one topic must be selected.");
        }
        if (settings.getSemesters() == null || settings.getSemesters().isEmpty()) {
            result.addError("At least one semester must be selected.");
        }
        if (settings.getSemesters() != null) {
            for (Integer semester : settings.getSemesters()) {
                if (semester == null || (semester != 1 && semester != 2)) {
                    result.addError("Semester must be 1, 2, or both.");
                    break;
                }
            }
        }
        if (!TextUtil.isBlank(settings.getTimetableName()) && repository.timetableNameExists(settings.getTimetableName())) {
            result.addError("Timetable name must be unique.");
        }
        return result;
    }

    public List<Timetable> browseTimetables() {
        return repository.listTimetables();
    }

    public Optional<Timetable> viewTimetable(String name) {
        return repository.findTimetable(name);
    }

    public PendingSwapResult prepareSwap(String timetableName, String existingGroupKey, String replacementGroupKey) {
        Optional<Timetable> existingTimetable = repository.findTimetable(timetableName);
        if (existingTimetable.isEmpty()) {
            return PendingSwapResult.failure("Timetable could not be found.");
        }

        Timetable proposed = existingTimetable.get().copy();
        List<ClassRecord> existingGroup = proposed.getRecords().stream()
                .filter(record -> record.groupKey().equals(existingGroupKey))
                .toList();
        List<ClassRecord> replacementGroup = repository.listClassRecords().stream()
                .filter(record -> record.groupKey().equals(replacementGroupKey))
                .toList();

        if (existingGroup.isEmpty() || replacementGroup.isEmpty()) {
            return PendingSwapResult.failure("Both the existing class and replacement class must exist.");
        }
        if (!canSwap(existingGroup.get(0), replacementGroup.get(0))) {
            return PendingSwapResult.failure("Replacement must have the same topic and class format.");
        }

        proposed.removeGroup(existingGroupKey);
        proposed.addRecords(replacementGroup);
        List<ScheduleWarning> warnings = scheduleService.findWarnings(proposed.getRecords(), lastSettings.isAllowLectureOverlap());
        return new PendingSwapResult(true, !warnings.isEmpty(), proposed,
                warnings.isEmpty() ? "Swap can be applied." : "Swap has warning(s) and requires confirmation.", warnings);
    }

    public void applySwap(PendingSwapResult pendingSwapResult) {
        if (pendingSwapResult != null && pendingSwapResult.canApply()) {
            repository.saveTimetable(pendingSwapResult.getProposedTimetable());
        }
    }

    public boolean deleteTimetable(String name) {
        return repository.deleteTimetable(name);
    }

    public TimetableSettings getLastSettings() {
        return lastSettings.copy();
    }

    private String resolveTimetableName(String requestedName) {
        if (!TextUtil.isBlank(requestedName)) {
            return requestedName.trim();
        }
        int count = repository.listTimetables().size() + 1;
        String generated = "Timetable " + count;
        while (repository.timetableNameExists(generated)) {
            count++;
            generated = "Timetable " + count;
        }
        return generated;
    }

    private List<ClassRecord> filterRecords(List<ClassRecord> records, TimetableSettings settings) {
        return records.stream()
                .filter(record -> settings.getTopicCodes().stream().anyMatch(topic -> TextUtil.equalsIgnoreCase(topic, record.getTopicCode())))
                .filter(record -> settings.getSemesters().contains(record.getSemester()))
                .filter(record -> settings.getCampuses().isEmpty()
                        || settings.getCampuses().stream().anyMatch(campus -> TextUtil.equalsIgnoreCase(campus, record.getCampus())))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Map<String, List<ClassGroup>> buildCandidatesByChoice(List<ClassRecord> records) {
        Map<String, ClassGroup> groups = new LinkedHashMap<>();
        for (ClassRecord record : records.stream().sorted(recordComparator()).toList()) {
            String name = "";
            groups.computeIfAbsent(record.groupKey(), key -> new ClassGroup(key, name)).addRecord(record);
        }
        Map<String, List<ClassGroup>> candidates = new LinkedHashMap<>();
        for (ClassGroup group : groups.values()) {
            candidates.computeIfAbsent(group.firstRecord().choiceKey(), key -> new ArrayList<>()).add(group);
        }
        return candidates;
    }

    private ClassGroup chooseBestGroup(List<ClassGroup> candidateGroups, List<ClassRecord> selectedRecords, TimetableSettings settings) {
        ClassGroup best = null;
        int bestWarningCount = Integer.MAX_VALUE;
        int bestScore = Integer.MIN_VALUE;

        for (ClassGroup candidate : candidateGroups) {
            if (!keepsCityCampusRule(candidate.getRecords(), selectedRecords)) {
                continue;
            }
            List<ClassRecord> proposed = new ArrayList<>(selectedRecords);
            proposed.addAll(candidate.getRecords());
            int warningCount = scheduleService.findWarnings(proposed, settings.isAllowLectureOverlap()).size();
            int score = preferenceScorer.score(candidate.getRecords(), settings, proposed);
            if (warningCount < bestWarningCount || (warningCount == bestWarningCount && score > bestScore)) {
                best = candidate;
                bestWarningCount = warningCount;
                bestScore = score;
            }
        }
        return best;
    }

    private boolean keepsCityCampusRule(List<ClassRecord> candidateRecords, List<ClassRecord> selectedRecords) {
        String topicCode = candidateRecords.get(0).getTopicCode();
        Set<Boolean> cityStatus = new HashSet<>();
        for (ClassRecord record : selectedRecords) {
            if (TextUtil.equalsIgnoreCase(record.getTopicCode(), topicCode)) {
                cityStatus.add(isCityCampus(record.getCampus()));
            }
        }
        for (ClassRecord record : candidateRecords) {
            cityStatus.add(isCityCampus(record.getCampus()));
        }
        return cityStatus.size() <= 1;
    }

    private boolean isCityCampus(String campus) {
        return campus != null && campus.toLowerCase().contains("city");
    }

    private boolean canSwap(ClassRecord existing, ClassRecord replacement) {
        return TextUtil.equalsIgnoreCase(existing.getTopicCode(), replacement.getTopicCode())
                && TextUtil.equalsIgnoreCase(existing.getClassFormat(), replacement.getClassFormat());
    }

    private Comparator<ClassRecord> recordComparator() {
        return Comparator.comparing(ClassRecord::getTopicCode)
                .thenComparing(ClassRecord::getClassFormat)
                .thenComparingInt(ClassRecord::getClassInstance)
                .thenComparing(ClassRecord::getFirstClassDate);
    }
}
