package edu.flinders.timetable.service;

import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.TimetableSettings;
import edu.flinders.timetable.result.TimetableGenerationResult;
import edu.flinders.timetable.result.ValidationResult;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class TimetableServiceTest {

    private DataRepository repository;
    private TimetableService service;

    @BeforeEach
    void setUp() {
        // this creates a fresh repository and timetable service before each test.
        repository = new DataRepository();
        service = new TimetableService(repository, new ScheduleService());
    }

    private ClassRecord record(String topicCode, String classFormat, int instance, String campus, DayOfWeek day, LocalTime start, LocalTime end) {
        // this helper creates a simple class record for timetable generation tests.
        return new ClassRecord(
                topicCode,
                "Game Design",
                "In person",
                campus,
                2,
                1,
                classFormat,
                instance,
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                day,
                start,
                end,
                campus + " Building",
                "Room"
        );
    }

    private TimetableSettings settings(String name) {
        // this creates basic valid timetable settings for one topic, one semester, and one campus.
        TimetableSettings settings = new TimetableSettings();
        settings.setTimetableName(name);
        settings.setTopicCodes(Set.of("COMP1701"));
        settings.setSemesters(Set.of(2));
        settings.setCampuses(Set.of("Tonsley"));
        return settings;
    }

    @Test
    @Tag("homv0001")
    @Tag("Critical")
    @DisplayName("TS7.01 - Null timetable settings are invalid")
    void ts701NullTimetableSettingsAreInvalid() {
        // this asks the service to validate null settings.
        ValidationResult result = service.validateSettings(null);

        // this checks that null settings are rejected.
        assertFalse(result.isValid());
    }

    @Test
    @Tag("homv0001")
    @Tag("Critical")
    @DisplayName("TS7.02 - Empty topic selection is invalid")
    void ts702EmptyTopicSelectionIsInvalid() {
        // this starts with valid settings.
        TimetableSettings settings = settings("Empty Topic Test");

        // this removes all selected topics, which should be invalid.
        settings.setTopicCodes(Set.of());

        // this validates the settings.
        ValidationResult result = service.validateSettings(settings);

        // this checks that timetable generation settings need at least one topic.
        assertFalse(result.isValid());
    }

    @Test
    @Tag("homv0001")
    @Tag("Critical")
    @DisplayName("TS7.03 - Invalid semester is rejected")
    void ts703InvalidSemesterIsRejected() {
        // this starts with valid settings.
        TimetableSettings settings = settings("Bad Semester Test");

        // this sets semester 3, which is not allowed by the assignment specification.
        settings.setSemesters(Set.of(3));

        // this validates the settings.
        ValidationResult result = service.validateSettings(settings);

        // this checks that only semester 1 and/or 2 are accepted.
        assertFalse(result.isValid());
    }

    @Test
    @Tag("homv0001")
    @Tag("Critical")
    @DisplayName("TS7.04 - Generate timetable saves valid timetable")
    void ts704GenerateTimetableSavesValidTimetable() {
        // this adds one class that matches the timetable settings.
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));

        // this generates the timetable.
        TimetableGenerationResult result = service.generateTimetable(settings("My Timetable"));

        // this checks that the timetable was generated and saved in the repository.
        assertAll(
                () -> assertTrue(result.isSuccess()),
                () -> assertNotNull(result.getTimetable()),
                () -> assertEquals("My Timetable", result.getTimetable().getName()),
                () -> assertTrue(repository.findTimetable("My Timetable").isPresent())
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("TS7.05 - Blank timetable name is automatically generated")
    void ts705BlankTimetableNameIsAutomaticallyGenerated() {
        // this adds one class that matches the timetable settings.
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));

        // this passes a blank timetable name.
        TimetableGenerationResult result = service.generateTimetable(settings("   "));

        // this checks that the service creates a default timetable name.
        assertAll(
                () -> assertTrue(result.isSuccess()),
                () -> assertEquals("Timetable 1", result.getTimetable().getName())
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("TS7.06 - Duplicate timetable name is rejected")
    void ts706DuplicateTimetableNameIsRejected() {
        // this adds one matching class.
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));

        // this creates the first timetable using the name Duplicate Test.
        service.generateTimetable(settings("Duplicate Test"));

        // this validates the same name again.
        ValidationResult result = service.validateSettings(settings("Duplicate Test"));

        // this checks that the same timetable name cannot be reused.
        assertFalse(result.isValid());
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("TS7.07 - Generate timetable fails when no classes match")
    void ts707GenerateTimetableFailsWhenNoClassesMatch() {
        // this imports a class for COMP9999, not COMP1701.
        repository.importRecords(List.of(record("COMP9999", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));

        // this tries to generate a timetable for COMP1701.
        TimetableGenerationResult result = service.generateTimetable(settings("No Match Test"));

        // this checks that generation fails because no imported classes match the selected settings.
        assertFalse(result.isSuccess());
    }
}
