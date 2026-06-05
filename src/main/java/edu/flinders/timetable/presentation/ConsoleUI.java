package edu.flinders.timetable.presentation;

import edu.flinders.timetable.application.ClassManager;
import edu.flinders.timetable.application.ImportManager;
import edu.flinders.timetable.application.TimetableManager;
import edu.flinders.timetable.data.CsvFormatException;
import edu.flinders.timetable.model.ClassGroup;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.PreferenceType;
import edu.flinders.timetable.model.SearchCriteria;
import edu.flinders.timetable.model.Timetable;
import edu.flinders.timetable.model.TimetableSettings;
import edu.flinders.timetable.result.ImportResult;
import edu.flinders.timetable.result.PendingSwapResult;
import edu.flinders.timetable.result.ScheduleWarning;
import edu.flinders.timetable.result.TimetableGenerationResult;
import edu.flinders.timetable.util.TextUtil;

import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class ConsoleUI {
    private final ImportManager importManager;
    private final ClassManager classManager;
    private final TimetableManager timetableManager;
    private final MenuSystem menuSystem = new MenuSystem();
    private final InputHandler inputHandler = new InputHandler(new Scanner(System.in));

    public ConsoleUI(ImportManager importManager, ClassManager classManager, TimetableManager timetableManager) {
        this.importManager = importManager;
        this.classManager = classManager;
        this.timetableManager = timetableManager;
    }

    public void start() {
        menuSystem.printTitle();
        boolean running = true;
        while (running) {
            menuSystem.printMainMenu();
            int choice = inputHandler.readInt("Choose an option: ", -1);
            try {
                running = handleChoice(choice);
            } catch (RuntimeException e) {
                System.out.println(Ansi.RED + "Error: " + e.getMessage() + Ansi.RESET);
            }
        }
        System.out.println(Ansi.GREEN + "Goodbye." + Ansi.RESET);
    }

    private boolean handleChoice(int choice) {
        switch (choice) {
            case 1 -> importClasses();
            case 2 -> browseClasses();
            case 3 -> viewClasses();
            case 4 -> searchClasses();
            case 5 -> editClass();
            case 6 -> deleteClass();
            case 7 -> generateTimetable();
            case 8 -> browseTimetables();
            case 9 -> viewTimetable();
            case 10 -> editTimetable();
            case 11 -> deleteTimetable();
            case 12 -> exportTimetable();
            case 0 -> {
                return false;
            }
            default -> System.out.println(Ansi.YELLOW + "Please choose a valid menu option." + Ansi.RESET);
        }
        return true;
    }

    private void importClasses() {
        String path = inputHandler.readLine("CSV path: ");
        try {
            ImportResult result = importManager.importCsv(Path.of(path));
            System.out.println(Ansi.GREEN + "Import complete. New: " + result.getNewRecordCount()
                    + ", Updated: " + result.getUpdatedRecordCount() + Ansi.RESET);
        } catch (CsvFormatException e) {
            System.out.println(Ansi.RED + "CSV import failed: " + e.getMessage() + Ansi.RESET);
        }
    }

    private void browseClasses() {
        List<ClassGroup> groups = classManager.browseClasses();
        if (groups.isEmpty()) {
            System.out.println("No classes imported.");
            return;
        }
        for (int i = 0; i < groups.size(); i++) {
            System.out.println((i + 1) + ". " + groups.get(i).summary());
        }
    }

    private void viewClasses() {
        List<ClassRecord> records = classManager.viewClasses();
        for (ClassRecord record : records) {
            System.out.println(record.importKey());
            System.out.println("  " + record.displayLine());
        }
        if (records.isEmpty()) {
            System.out.println("No class records found.");
        }
    }

    private void searchClasses() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setTopicCode(blankToNull(inputHandler.readLine("Topic code (blank for any): ")));
        criteria.setTopicName(blankToNull(inputHandler.readLine("Topic name (blank for any): ")));
        criteria.setCampus(blankToNull(inputHandler.readLine("Campus (blank for any): ")));
        int semester = inputHandler.readInt("Semester 1/2 (-1 for any): ", -1);
        if (semester == 1 || semester == 2) {
            criteria.setSemester(semester);
        }
        criteria.setClassFormat(blankToNull(inputHandler.readLine("Class format (blank for any): ")));
        criteria.setBuilding(blankToNull(inputHandler.readLine("Building (blank for any): ")));
        criteria.setRoom(blankToNull(inputHandler.readLine("Room (blank for any): ")));

        List<ClassRecord> results = classManager.searchClasses(criteria);
        results.forEach(record -> System.out.println(record.importKey() + "\n  " + record.displayLine()));
        System.out.println(results.size() + " record(s) matched.");
    }

    private void editClass() {
        viewClasses();
        String key = inputHandler.readLine("Paste the import key of the class to edit: ");
        Optional<ClassRecord> existing = classManager.findClass(key);
        if (existing.isEmpty()) {
            System.out.println(Ansi.YELLOW + "Class record was not found." + Ansi.RESET);
            return;
        }
        if (!inputHandler.confirm("Warning: editing class data may affect generated timetables. Continue?")) {
            System.out.println("Edit cancelled.");
            return;
        }

        ClassRecord updated = existing.get().copy();
        String topicCode = inputHandler.readLine("Topic code [" + updated.getTopicCode() + "]: ");
        updated.setTopicCode(TextUtil.firstNonBlank(topicCode, updated.getTopicCode()));
        String topicName = inputHandler.readLine("Topic name [" + updated.getTopicName() + "]: ");
        updated.setTopicName(TextUtil.firstNonBlank(topicName, updated.getTopicName()));
        String campus = inputHandler.readLine("Campus [" + updated.getCampus() + "]: ");
        updated.setCampus(TextUtil.firstNonBlank(campus, updated.getCampus()));
        String classFormat = inputHandler.readLine("Class format [" + updated.getClassFormat() + "]: ");
        updated.setClassFormat(TextUtil.firstNonBlank(classFormat, updated.getClassFormat()));
        String building = inputHandler.readLine("Building [" + updated.getBuilding() + "]: ");
        updated.setBuilding(TextUtil.firstNonBlank(building, updated.getBuilding()));
        String room = inputHandler.readLine("Room [" + updated.getRoom() + "]: ");
        updated.setRoom(TextUtil.firstNonBlank(room, updated.getRoom()));

        boolean saved = classManager.editClass(key, updated);
        System.out.println(saved ? Ansi.GREEN + "Class updated." + Ansi.RESET : Ansi.RED + "Update failed." + Ansi.RESET);
    }

    private void deleteClass() {
        viewClasses();
        String key = inputHandler.readLine("Paste the import key of the class to delete: ");
        if (!inputHandler.confirm("Warning: this will delete the selected class record. Continue?")) {
            System.out.println("Delete cancelled.");
            return;
        }
        boolean deleted = classManager.deleteClass(key);
        System.out.println(deleted ? Ansi.GREEN + "Class deleted." + Ansi.RESET : Ansi.YELLOW + "Class was not found." + Ansi.RESET);
    }

    private void generateTimetable() {
        TimetableSettings previous = timetableManager.getLastSettings();
        TimetableSettings settings = new TimetableSettings();
        settings.setTimetableName(inputHandler.readLine("Timetable name (blank to auto-generate): "));
        settings.setSemesters(readSemesters());
        settings.setTopicCodes(inputHandler.readCsvValues("Topic codes to include, comma separated: "));
        settings.setCampuses(inputHandler.readCsvValues("Campuses, comma separated (blank for any): "));
        settings.setAllowLectureOverlap(inputHandler.confirm("Allow lecture overlap?"));
        settings.setPreferences(readPreferences());

        if (settings.getSemesters().isEmpty()) {
            settings.setSemesters(previous.getSemesters().isEmpty() ? Set.of(1, 2) : previous.getSemesters());
        }

        TimetableGenerationResult result = timetableManager.generateTimetable(settings);
        System.out.println(result.isSuccess() ? Ansi.GREEN + result.getMessage() + Ansi.RESET : Ansi.RED + result.getMessage() + Ansi.RESET);
        if (result.isSuccess()) {
            printWarnings(result.getWarnings());
        }
    }

    private Set<Integer> readSemesters() {
        String input = inputHandler.readLine("Semester (1, 2, or both): ");
        Set<Integer> semesters = new LinkedHashSet<>();
        if (input.equalsIgnoreCase("both")) {
            semesters.add(1);
            semesters.add(2);
        } else if (input.equals("1")) {
            semesters.add(1);
        } else if (input.equals("2")) {
            semesters.add(2);
        }
        return semesters;
    }

    private List<PreferenceType> readPreferences() {
        System.out.println("Preferences can be entered as numbers, comma separated, highest priority first.");
        PreferenceType[] values = PreferenceType.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + values[i].getDisplayName());
        }
        String input = inputHandler.readLine("Preferences (blank for none): ");
        List<PreferenceType> selected = new ArrayList<>();
        if (TextUtil.isBlank(input)) {
            return selected;
        }
        for (String part : input.split(",")) {
            try {
                int index = Integer.parseInt(part.trim()) - 1;
                if (index >= 0 && index < values.length) {
                    selected.add(values[index]);
                }
            } catch (NumberFormatException ignored) {
                // Invalid preference numbers are ignored to keep the console flow simple.
            }
        }
        return selected;
    }

    private void browseTimetables() {
        List<Timetable> timetables = timetableManager.browseTimetables();
        if (timetables.isEmpty()) {
            System.out.println("No timetables have been generated.");
            return;
        }
        for (Timetable timetable : timetables) {
            System.out.println(timetable.getName() + " | " + timetable.getRecords().size() + " class record(s)");
        }
    }

    private void viewTimetable() {
        String name = inputHandler.readLine("Timetable name: ");
        Optional<Timetable> timetable = timetableManager.viewTimetable(name);
        if (timetable.isEmpty()) {
            System.out.println(Ansi.YELLOW + "Timetable was not found." + Ansi.RESET);
            return;
        }
        printTimetable(timetable.get());
    }

    private void editTimetable() {
        String name = inputHandler.readLine("Timetable name: ");
        Optional<Timetable> timetable = timetableManager.viewTimetable(name);
        if (timetable.isEmpty()) {
            System.out.println(Ansi.YELLOW + "Timetable was not found." + Ansi.RESET);
            return;
        }
        printTimetable(timetable.get());
        String existingGroupKey = inputHandler.readLine("Existing group key to swap out: ");
        String replacementGroupKey = inputHandler.readLine("Replacement group key: ");
        PendingSwapResult result = timetableManager.prepareSwap(name, existingGroupKey, replacementGroupKey);
        if (!result.canApply()) {
            System.out.println(Ansi.RED + result.getMessage() + Ansi.RESET);
            return;
        }
        if (result.requiresConfirmation()) {
            printWarnings(result.getWarnings());
            if (!inputHandler.confirm("Warning: this swap creates a clash or commute issue. Apply anyway?")) {
                System.out.println("Swap cancelled.");
                return;
            }
        }
        timetableManager.applySwap(result);
        System.out.println(Ansi.GREEN + "Timetable updated." + Ansi.RESET);
    }

    private void deleteTimetable() {
        String name = inputHandler.readLine("Timetable name to delete: ");
        if (!inputHandler.confirm("Warning: this will delete the timetable. Continue?")) {
            System.out.println("Delete cancelled.");
            return;
        }
        boolean deleted = timetableManager.deleteTimetable(name);
        System.out.println(deleted ? Ansi.GREEN + "Timetable deleted." + Ansi.RESET : Ansi.YELLOW + "Timetable was not found." + Ansi.RESET);
    }

    private void exportTimetable() {
        String name = inputHandler.readLine("Timetable name to export: ");
        Optional<Timetable> timetable = timetableManager.viewTimetable(name);
        if (timetable.isEmpty()) {
            System.out.println(Ansi.YELLOW + "Timetable was not found." + Ansi.RESET);
            return;
        }
        String defaultPath = "exports/" + name.replaceAll("[^a-zA-Z0-9-_]", "_") + ".csv";
        String output = inputHandler.readLine("Output CSV path [" + defaultPath + "]: ");
        Path exported = timetableManager.exportTimetable(timetable.get(), Path.of(TextUtil.isBlank(output) ? defaultPath : output));
        System.out.println(Ansi.GREEN + "Exported to " + exported + Ansi.RESET);
    }

    private void printTimetable(Timetable timetable) {
        System.out.println(Ansi.BOLD + Ansi.UNDERLINE + timetable.getName() + Ansi.RESET);
        for (ClassRecord record : timetable.getRecords()) {
            System.out.println("Group key: " + record.groupKey());
            System.out.println("  " + record.displayLine());
        }
    }

    private void printWarnings(List<ScheduleWarning> warnings) {
        if (warnings.isEmpty()) {
            return;
        }
        System.out.println(Ansi.YELLOW + "Schedule warning(s):" + Ansi.RESET);
        for (ScheduleWarning warning : warnings) {
            System.out.println("- " + warning.getMessage());
        }
    }

    private String blankToNull(String value) {
        return TextUtil.isBlank(value) ? null : value;
    }
}
