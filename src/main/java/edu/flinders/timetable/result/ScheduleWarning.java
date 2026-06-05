package edu.flinders.timetable.result;

import edu.flinders.timetable.model.ClassRecord;

public class ScheduleWarning {
    public enum Type {
        TIME_CLASH,
        COMMUTE_GAP
    }

    private final Type type;
    private final ClassRecord firstClass;
    private final ClassRecord secondClass;
    private final String message;

    public ScheduleWarning(Type type, ClassRecord firstClass, ClassRecord secondClass, String message) {
        this.type = type;
        this.firstClass = firstClass;
        this.secondClass = secondClass;
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public ClassRecord getFirstClass() {
        return firstClass;
    }

    public ClassRecord getSecondClass() {
        return secondClass;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return type + ": " + message;
    }
}
