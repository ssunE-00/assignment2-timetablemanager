package edu.flinders.timetable.result;

import edu.flinders.timetable.model.Timetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PendingSwapResult {
    private final boolean canApply;
    private final boolean requiresConfirmation;
    private final Timetable proposedTimetable;
    private final String message;
    private final List<ScheduleWarning> warnings;

    public PendingSwapResult(boolean canApply, boolean requiresConfirmation, Timetable proposedTimetable,
                             String message, List<ScheduleWarning> warnings) {
        this.canApply = canApply;
        this.requiresConfirmation = requiresConfirmation;
        this.proposedTimetable = proposedTimetable;
        this.message = message;
        this.warnings = new ArrayList<>(warnings);
    }

    public static PendingSwapResult failure(String message) {
        return new PendingSwapResult(false, false, null, message, List.of());
    }

    public boolean canApply() {
        return canApply;
    }

    public boolean requiresConfirmation() {
        return requiresConfirmation;
    }

    public Timetable getProposedTimetable() {
        return proposedTimetable;
    }

    public String getMessage() {
        return message;
    }

    public List<ScheduleWarning> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
}
