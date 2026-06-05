package edu.flinders.timetable.util;

import java.time.Duration;
import java.time.LocalTime;

public final class TimeUtil {
    private TimeUtil() {
    }

    public static boolean overlaps(LocalTime startA, LocalTime endA, LocalTime startB, LocalTime endB) {
        return startA.isBefore(endB) && startB.isBefore(endA);
    }

    public static long minutesBetween(LocalTime firstEnd, LocalTime secondStart) {
        return Duration.between(firstEnd, secondStart).toMinutes();
    }
}
