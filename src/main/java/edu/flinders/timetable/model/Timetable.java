package edu.flinders.timetable.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Timetable {
    private String name;
    private final List<ClassRecord> records = new ArrayList<>();
    private LocalDateTime createdAt;

    public Timetable(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public Timetable(String name, List<ClassRecord> records) {
        this(name);
        this.records.addAll(records);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ClassRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void addRecords(List<ClassRecord> newRecords) {
        records.addAll(newRecords);
    }

    public void removeGroup(String groupKey) {
        records.removeIf(record -> record.groupKey().equals(groupKey));
    }

    public Map<String, List<ClassRecord>> recordsByGroupKey() {
        return records.stream().collect(Collectors.groupingBy(ClassRecord::groupKey));
    }

    public Timetable copy() {
        List<ClassRecord> copies = records.stream().map(ClassRecord::copy).collect(Collectors.toCollection(ArrayList::new));
        Timetable copy = new Timetable(name, copies);
        copy.createdAt = this.createdAt;
        return copy;
    }
}
