package edu.flinders.timetable.service;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.result.ScheduleWarning;
import edu.flinders.timetable.util.TextUtil;
import edu.flinders.timetable.util.TimeUtil;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScheduleService {
    private static final long MINIMUM_COMMUTE_MINUTES = 30;

    public List<ScheduleWarning> findWarnings(List<ClassRecord> records, boolean allowLectureOverlap) {
        List<ClassRecord> sorted = records.stream()
                .sorted(Comparator.comparing(ClassRecord::getDay).thenComparing(ClassRecord::getStartTime))
                .toList();

        List<ScheduleWarning> warnings = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            for (int j = i + 1; j < sorted.size(); j++) {
                ClassRecord first = sorted.get(i);
                ClassRecord second = sorted.get(j);
                if (first.getDay() != second.getDay()) {
                    continue;
                }
                if (allowLectureOverlap && (first.isLecture() || second.isLecture())) {
                    continue;
                }
                addPairWarningIfNeeded(first, second, warnings);
            }
        }
        return warnings;
    }

    private void addPairWarningIfNeeded(ClassRecord first, ClassRecord second, List<ScheduleWarning> warnings) {
        if (TimeUtil.overlaps(first.getStartTime(), first.getEndTime(), second.getStartTime(), second.getEndTime())) {
            warnings.add(new ScheduleWarning(ScheduleWarning.Type.TIME_CLASH, first, second,
                    first.displayLine() + " clashes with " + second.displayLine()));
            return;
        }

        if (TextUtil.equalsIgnoreCase(first.getCampus(), second.getCampus())) {
            return;
        }

        ClassRecord earlier = first.getStartTime().isBefore(second.getStartTime()) ? first : second;
        ClassRecord later = earlier == first ? second : first;
        long gap = minutesBetween(earlier.getEndTime(), later.getStartTime());
        if (gap >= 0 && gap < MINIMUM_COMMUTE_MINUTES) {
            warnings.add(new ScheduleWarning(ScheduleWarning.Type.COMMUTE_GAP, earlier, later,
                    "Only " + gap + " minute(s) between different campuses: "
                            + earlier.displayLine() + " then " + later.displayLine()));
        }
    }

    private long minutesBetween(LocalTime end, LocalTime start) {
        return TimeUtil.minutesBetween(end, start);
    }
}
