package edu.flinders.timetable.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImportResult {
    private int newRecordCount;
    private int updatedRecordCount;
    private final List<String> errors = new ArrayList<>();

    public void incrementNewRecordCount() {
        newRecordCount++;
    }

    public void incrementUpdatedRecordCount() {
        updatedRecordCount++;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public int getNewRecordCount() {
        return newRecordCount;
    }

    public int getUpdatedRecordCount() {
        return updatedRecordCount;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
