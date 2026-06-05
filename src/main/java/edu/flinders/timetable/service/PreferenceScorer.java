package edu.flinders.timetable.service;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.PreferenceType;
import edu.flinders.timetable.model.TimetableSettings;
import edu.flinders.timetable.util.TextUtil;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreferenceScorer {
    public int score(List<ClassRecord> candidateRecords, TimetableSettings settings, List<ClassRecord> proposedTimetable) {
        int score = 0;
        int weight = settings.getPreferences().size();
        for (PreferenceType preference : settings.getPreferences()) {
            score += matches(preference, candidateRecords, proposedTimetable) ? weight * 10 : 0;
            weight--;
        }
        return score;
    }

    private boolean matches(PreferenceType preference, List<ClassRecord> candidateRecords, List<ClassRecord> proposedTimetable) {
        return switch (preference) {
            case BEDFORD_PARK -> allAtCampus(candidateRecords, "Bedford Park");
            case TONSLEY -> allAtCampus(candidateRecords, "Tonsley");
            case FLINDERS_CITY -> allAtCampus(candidateRecords, "Flinders City Campus");
            case SAME_CAMPUS -> allProposedAtSameCampus(proposedTimetable);
            case MORNINGS -> candidateRecords.stream().allMatch(record -> record.getStartTime().isBefore(LocalTime.NOON));
            case AFTERNOONS -> candidateRecords.stream().allMatch(record -> !record.getStartTime().isBefore(LocalTime.NOON));
            case MONDAY -> allOnDay(candidateRecords, DayOfWeek.MONDAY);
            case TUESDAY -> allOnDay(candidateRecords, DayOfWeek.TUESDAY);
            case WEDNESDAY -> allOnDay(candidateRecords, DayOfWeek.WEDNESDAY);
            case THURSDAY -> allOnDay(candidateRecords, DayOfWeek.THURSDAY);
            case FRIDAY -> allOnDay(candidateRecords, DayOfWeek.FRIDAY);
            case EVENLY_SPREAD -> countDays(proposedTimetable) >= 3;
            case COMPACT_DAYS -> countDays(proposedTimetable) <= 2;
        };
    }

    private boolean allAtCampus(List<ClassRecord> records, String campus) {
        return records.stream().allMatch(record -> TextUtil.equalsIgnoreCase(record.getCampus(), campus));
    }

    private boolean allOnDay(List<ClassRecord> records, DayOfWeek day) {
        return records.stream().allMatch(record -> record.getDay() == day);
    }

    private boolean allProposedAtSameCampus(List<ClassRecord> records) {
        Set<String> campuses = new HashSet<>();
        for (ClassRecord record : records) {
            campuses.add(record.getCampus().toLowerCase());
        }
        return campuses.size() <= 1;
    }

    private int countDays(List<ClassRecord> records) {
        return records.stream().map(ClassRecord::getDay).collect(java.util.stream.Collectors.toSet()).size();
    }
}
