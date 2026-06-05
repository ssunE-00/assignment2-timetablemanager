package edu.flinders.timetable.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassGroup {
    private final String key;
    private final List<ClassRecord> records = new ArrayList<>();

    public ClassGroup(String key, String name) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void addRecord(ClassRecord record) {
        records.add(record);
    }

    public List<ClassRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public ClassRecord firstRecord() {
        if (records.isEmpty()) {
            throw new IllegalStateException("Class group has no records");
        }
        return records.get(0);
    }

    public String summary() {
        ClassRecord first = firstRecord();
        return first.getTopicCode() + " " + first.getTopicName()
                + " | " + first.getAttendanceMode()
                + " | " + first.getCampus()
                + " | S" + first.getSemester()
                + " | Availability " + first.getAvailabilityNumber()
                + " | " + first.getClassFormat()
                + " | Instance " + first.getClassInstance()
                + " | " + records.size() + " date record(s)";
    }
}
