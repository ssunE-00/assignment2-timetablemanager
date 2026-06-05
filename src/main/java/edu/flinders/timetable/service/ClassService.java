package edu.flinders.timetable.service;

import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.model.ClassGroup;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.SearchCriteria;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClassService {
    protected final DataRepository repository;

    public ClassService(DataRepository repository) {
        this.repository = repository;
    }

    public List<ClassGroup> browseClasses() {
        return groupRecords(repository.listClassRecords());
    }

    public List<ClassRecord> viewClasses() {
        return repository.listClassRecords().stream()
                .sorted(recordComparator())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<ClassRecord> searchClasses(SearchCriteria criteria) {
        SearchCriteria safeCriteria = criteria == null ? new SearchCriteria() : criteria;
        return repository.listClassRecords().stream()
                .filter(safeCriteria::matches)
                .sorted(recordComparator())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Optional<ClassRecord> findClassByKey(String importKey) {
        return repository.findClassByImportKey(importKey);
    }

    public boolean editClass(String importKey, ClassRecord updatedRecord) {
        return repository.replaceClass(importKey, updatedRecord);
    }

    public boolean deleteClass(String importKey) {
        return repository.deleteClass(importKey);
    }

    private List<ClassGroup> groupRecords(List<ClassRecord> records) {
        Map<String, ClassGroup> groups = new LinkedHashMap<>();
        for (ClassRecord record : records.stream().sorted(recordComparator()).toList()) {
            String name = "";
            groups.computeIfAbsent(record.groupKey(), key -> new ClassGroup(key, name)).addRecord(record);
        }
        return new ArrayList<>(groups.values());
    }

    private Comparator<ClassRecord> recordComparator() {
        return Comparator.comparing(ClassRecord::getTopicCode)
                .thenComparing(ClassRecord::getCampus)
                .thenComparingInt(ClassRecord::getSemester)
                .thenComparing(ClassRecord::getClassFormat)
                .thenComparingInt(ClassRecord::getClassInstance)
                .thenComparing(ClassRecord::getFirstClassDate);
    }
}
