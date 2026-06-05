package edu.flinders.timetable.application;

import edu.flinders.timetable.model.ClassGroup;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.SearchCriteria;
import edu.flinders.timetable.service.ClassService;

import java.util.List;
import java.util.Optional;

public class ClassManager {
    private final ClassService classService;

    public ClassManager(ClassService classService) {
        this.classService = classService;
    }

    public List<ClassGroup> browseClasses() {
        return classService.browseClasses();
    }

    public List<ClassRecord> viewClasses() {
        return classService.viewClasses();
    }

    public List<ClassRecord> searchClasses(SearchCriteria criteria) {
        return classService.searchClasses(criteria);
    }

    public Optional<ClassRecord> findClass(String importKey) {
        return classService.findClassByKey(importKey);
    }

    public boolean editClass(String importKey, ClassRecord updatedRecord) {
        return classService.editClass(importKey, updatedRecord);
    }

    public boolean deleteClass(String importKey) {
        return classService.deleteClass(importKey);
    }
}
