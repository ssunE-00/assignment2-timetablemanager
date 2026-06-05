package edu.flinders.timetable.data;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.Timetable;
import edu.flinders.timetable.result.ImportResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DataRepository {
    private final List<ClassRecord> classRecords = new ArrayList<>();
    private final Map<String, Timetable> timetablesByName = new LinkedHashMap<>();

    public ImportResult importRecords(List<ClassRecord> records) {
        ImportResult result = new ImportResult();
        for (ClassRecord incoming : records) {
            Optional<ClassRecord> existing = findClassByImportKey(incoming.importKey());
            if (existing.isPresent()) {
                updateTimeAndLocation(existing.get(), incoming);
                result.incrementUpdatedRecordCount();
            } else {
                classRecords.add(incoming);
                result.incrementNewRecordCount();
            }
        }
        return result;
    }

    public List<ClassRecord> listClassRecords() {
        return new ArrayList<>(classRecords);
    }

    public Optional<ClassRecord> findClassByImportKey(String importKey) {
        return classRecords.stream()
                .filter(record -> record.importKey().equals(importKey))
                .findFirst();
    }

    public boolean replaceClass(String importKey, ClassRecord updatedRecord) {
        for (int i = 0; i < classRecords.size(); i++) {
            if (classRecords.get(i).importKey().equals(importKey)) {
                classRecords.set(i, updatedRecord);
                return true;
            }
        }
        return false;
    }

    public boolean deleteClass(String importKey) {
        return classRecords.removeIf(record -> record.importKey().equals(importKey));
    }

    public void saveTimetable(Timetable timetable) {
        timetablesByName.put(timetable.getName(), timetable);
    }

    public boolean timetableNameExists(String name) {
        return timetablesByName.containsKey(name);
    }

    public List<Timetable> listTimetables() {
        return new ArrayList<>(timetablesByName.values());
    }

    public Optional<Timetable> findTimetable(String name) {
        return Optional.ofNullable(timetablesByName.get(name));
    }

    public boolean deleteTimetable(String name) {
        return timetablesByName.remove(name) != null;
    }

    private void updateTimeAndLocation(ClassRecord existing, ClassRecord incoming) {
        existing.setStartTime(incoming.getStartTime());
        existing.setEndTime(incoming.getEndTime());
        existing.setBuilding(incoming.getBuilding());
        existing.setRoom(incoming.getRoom());
    }
}
