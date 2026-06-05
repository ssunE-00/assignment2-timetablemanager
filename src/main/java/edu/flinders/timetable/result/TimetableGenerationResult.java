package edu.flinders.timetable.result;

import edu.flinders.timetable.model.Timetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimetableGenerationResult {
    private final boolean success;
    private final Timetable timetable;
    private final String message;
    private final List<ScheduleWarning> warnings;

    public TimetableGenerationResult(boolean success, Timetable timetable, String message, List<ScheduleWarning> warnings) {
        this.success = success;
        this.timetable = timetable;
        this.message = message;
        this.warnings = new ArrayList<>(warnings);
    }

    public static TimetableGenerationResult success(Timetable timetable, List<ScheduleWarning> warnings) {
        return new TimetableGenerationResult(true, timetable, "Timetable generated successfully.", warnings);
    }

    public static TimetableGenerationResult failure(String message) {
        return new TimetableGenerationResult(false, null, message, List.of());
    }

    public boolean isSuccess() {
        return success;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    public String getMessage() {
        return message;
    }

    public List<ScheduleWarning> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
}
