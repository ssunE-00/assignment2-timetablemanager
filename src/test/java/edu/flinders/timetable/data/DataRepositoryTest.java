package edu.flinders.timetable.data;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.Timetable;
import edu.flinders.timetable.result.ImportResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class DataRepositoryTest {

    private DataRepository repository;

    @BeforeEach
    void setUp() {
        // this creates a fresh empty repository before each test so tests do not affect each other.
        repository = new DataRepository();
    }

    private ClassRecord record(String topicCode, String classFormat, int instance, LocalTime start, LocalTime end, String building, String room) {
        // this helper makes a simple ClassRecord so each test does not need to repeat the full constructor every time.
        return new ClassRecord(
                topicCode,
                "Game Design",
                "In person",
                "Tonsley",
                2,
                1,
                classFormat,
                instance,
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                DayOfWeek.MONDAY,
                start,
                end,
                building,
                room
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Critical")
    @DisplayName("DR2.01 - Import new class record increases new count")
    void dr201ImportNewClassRecordIncreasesNewCount() {
        // this creates one normal class record.
        ClassRecord newRecord = record("COMP1701", "Workshop", 1, LocalTime.of(9, 0), LocalTime.of(10, 0), "Tonsley T1", "1.08");

        // this imports the record into the repository.
        ImportResult result = repository.importRecords(List.of(newRecord));

        // this checks the repository counted it as one new record and saved it.
        assertAll(
                () -> assertEquals(1, result.getNewRecordCount()),
                () -> assertEquals(0, result.getUpdatedRecordCount()),
                () -> assertEquals(1, repository.listClassRecords().size())
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Critical")
    @DisplayName("DR2.02 - Import duplicate updates time and location")
    void dr202ImportDuplicateUpdatesTimeAndLocation() {
        // this is the original class record.
        ClassRecord original = record("COMP1701", "Workshop", 1, LocalTime.of(9, 0), LocalTime.of(10, 0), "Old Building", "Old Room");

        // this has the same import key, but a different time and location.
        ClassRecord updated = record("COMP1701", "Workshop", 1, LocalTime.of(11, 0), LocalTime.of(12, 0), "New Building", "New Room");

        // this imports the original first, then imports the duplicate.
        repository.importRecords(List.of(original));
        ImportResult secondResult = repository.importRecords(List.of(updated));

        // this gets the saved record after the duplicate import.
        ClassRecord stored = repository.listClassRecords().get(0);

        // this checks that no extra duplicate was created, and only time/location were updated.
        assertAll(
                () -> assertEquals(0, secondResult.getNewRecordCount()),
                () -> assertEquals(1, secondResult.getUpdatedRecordCount()),
                () -> assertEquals(1, repository.listClassRecords().size()),
                () -> assertEquals(LocalTime.of(11, 0), stored.getStartTime()),
                () -> assertEquals("New Building", stored.getBuilding()),
                () -> assertEquals("New Room", stored.getRoom())
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("DR2.03 - Find class by import key returns class")
    void dr203FindClassByImportKeyReturnsClass() {
        // this creates and imports one class record.
        ClassRecord classRecord = record("COMP1701", "Workshop", 1, LocalTime.of(9, 0), LocalTime.of(10, 0), "Tonsley T1", "1.08");
        repository.importRecords(List.of(classRecord));

        // this checks that the repository can find the class using its import key.
        assertTrue(repository.findClassByImportKey(classRecord.importKey()).isPresent());
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("DR2.04 - Replace existing class record")
    void dr204ReplaceExistingClassRecord() {
        // this creates the original record and puts it into the repository.
        ClassRecord original = record("COMP1701", "Workshop", 1, LocalTime.of(9, 0), LocalTime.of(10, 0), "Tonsley T1", "1.08");
        repository.importRecords(List.of(original));

        // this creates a replacement class record.
        ClassRecord replacement = record("COMP1701", "Tutorial", 2, LocalTime.of(13, 0), LocalTime.of(14, 0), "Festival Tower", "506");

        // this replaces the original record with the replacement.
        boolean replaced = repository.replaceClass(original.importKey(), replacement);

        // this checks that the replacement worked and that the new class details are saved.
        assertAll(
                () -> assertTrue(replaced),
                () -> assertEquals("Tutorial", repository.listClassRecords().get(0).getClassFormat()),
                () -> assertEquals(2, repository.listClassRecords().get(0).getClassInstance())
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("DR2.05 - Delete existing class record")
    void dr205DeleteExistingClassRecord() {
        // this creates and imports one class record.
        ClassRecord classRecord = record("COMP1701", "Workshop", 1, LocalTime.of(9, 0), LocalTime.of(10, 0), "Tonsley T1", "1.08");
        repository.importRecords(List.of(classRecord));

        // this deletes the class by using its import key.
        boolean deleted = repository.deleteClass(classRecord.importKey());

        // this checks that the delete operation worked and that the repository is now empty.
        assertAll(
                () -> assertTrue(deleted),
                () -> assertTrue(repository.listClassRecords().isEmpty())
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("DR2.06 - Delete missing class record returns false")
    void dr206DeleteMissingClassRecordReturnsFalse() {
        // this tries to delete a class that does not exist.
        boolean deleted = repository.deleteClass("missing-key");

        // this checks that the repository returns false instead of pretending the delete worked.
        assertFalse(deleted);
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("DR2.07 - Save find and delete timetable")
    void dr207SaveFindAndDeleteTimetable() {
        // this creates a simple empty timetable.
        Timetable timetable = new Timetable("My Timetable");

        // this saves the timetable into the repository.
        repository.saveTimetable(timetable);

        // this checks save, find, list, and delete for timetables in one simple test.
        assertAll(
                () -> assertTrue(repository.timetableNameExists("My Timetable")),
                () -> assertTrue(repository.findTimetable("My Timetable").isPresent()),
                () -> assertEquals(1, repository.listTimetables().size()),
                () -> assertTrue(repository.deleteTimetable("My Timetable")),
                () -> assertFalse(repository.findTimetable("My Timetable").isPresent())
        );
    }
}
