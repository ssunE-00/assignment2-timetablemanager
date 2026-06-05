package edu.flinders.timetable.service;

import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.Timetable;
import edu.flinders.timetable.model.TimetableSettings;
import edu.flinders.timetable.result.PendingSwapResult;
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
        repository = new DataRepository();
        service = new TimetableService(repository, new ScheduleService());
    }

    private ClassRecord record(String topicCode, String classFormat, int instance, String campus, DayOfWeek day, LocalTime start, LocalTime end) {
        return new ClassRecord(
                topicCode, "Game Design", "In person", campus, 2, 1,
                classFormat, instance,
                LocalDate.of(2026, 7, 27), LocalDate.of(2026, 9, 14),
                day, start, end,
                campus + " Building", "Room"
        );
    }

    private TimetableSettings settings(String name) {
        TimetableSettings settings = new TimetableSettings();
        settings.setTimetableName(name);
        settings.setTopicCodes(Set.of("COMP1701"));
        settings.setSemesters(Set.of(2));
        settings.setCampuses(Set.of("Tonsley"));
        return settings;
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TS7.01 - Null timetable settings are invalid")
    void ts701NullTimetableSettingsAreInvalid() {
        ValidationResult result = service.validateSettings(null);
        assertFalse(result.isValid());
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TS7.02 - Empty topic selection is invalid")
    void ts702EmptyTopicSelectionIsInvalid() {
        TimetableSettings settings = settings("Empty Topic Test");
        settings.setTopicCodes(Set.of());
        ValidationResult result = service.validateSettings(settings);
        assertFalse(result.isValid());
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TS7.03 - Invalid semester number is rejected")
    void ts703InvalidSemesterIsRejected() {
        TimetableSettings settings = settings("Bad Semester Test");
        settings.setSemesters(Set.of(3));
        ValidationResult result = service.validateSettings(settings);
        assertFalse(result.isValid());
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TS7.04 - Generate timetable saves valid timetable to repository")
    void ts704GenerateTimetableSavesValidTimetable() {
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));
        TimetableGenerationResult result = service.generateTimetable(settings("My Timetable"));
        assertAll(
                () -> assertTrue(result.isSuccess()),
                () -> assertNotNull(result.getTimetable()),
                () -> assertEquals("My Timetable", result.getTimetable().getName()),
                () -> assertTrue(repository.findTimetable("My Timetable").isPresent())
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TS7.05 - Blank timetable name is automatically generated")
    void ts705BlankTimetableNameIsAutomaticallyGenerated() {
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));
        TimetableGenerationResult result = service.generateTimetable(settings("   "));
        assertAll(
                () -> assertTrue(result.isSuccess()),
                () -> assertEquals("Timetable 1", result.getTimetable().getName())
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TS7.06 - Duplicate timetable name is rejected by validation")
    void ts706DuplicateTimetableNameIsRejected() {
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));
        service.generateTimetable(settings("Duplicate Test"));
        ValidationResult result = service.validateSettings(settings("Duplicate Test"));
        assertFalse(result.isValid());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TS7.07 - Generate timetable fails when no imported classes match settings")
    void ts707GenerateTimetableFailsWhenNoClassesMatch() {
        repository.importRecords(List.of(record("COMP9999", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));
        TimetableGenerationResult result = service.generateTimetable(settings("No Match Test"));
        assertFalse(result.isSuccess());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TS7.08 - Browse timetables returns saved timetables")
    void ts708BrowseTimetablesReturnsSavedTimetables() {
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));
        service.generateTimetable(settings("Browse Test"));
        List<Timetable> timetables = service.browseTimetables();
        assertEquals(1, timetables.size());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TS7.09 - View timetable returns present optional when timetable exists")
    void ts709ViewTimetableReturnsPresentOptional() {
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));
        service.generateTimetable(settings("View Test"));
        assertTrue(service.viewTimetable("View Test").isPresent());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TS7.10 - View timetable returns empty optional when timetable does not exist")
    void ts710ViewTimetableReturnsEmptyWhenNotFound() {
        assertTrue(service.viewTimetable("Nonexistent").isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TS7.11 - Delete timetable removes it from repository")
    void ts711DeleteTimetableRemovesIt() {
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));
        service.generateTimetable(settings("Delete Test"));
        assertTrue(service.deleteTimetable("Delete Test"));
        assertTrue(service.viewTimetable("Delete Test").isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TS7.12 - Delete timetable returns false when timetable does not exist")
    void ts712DeleteTimetableReturnsFalseWhenNotFound() {
        assertFalse(service.deleteTimetable("Nonexistent"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("TS7.13 - getLastSettings returns a copy of the last used settings")
    void ts713GetLastSettingsReturnsLastUsedSettings() {
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));
        service.generateTimetable(settings("Settings Test"));
        TimetableSettings last = service.getLastSettings();
        assertNotNull(last);
        assertTrue(last.getTopicCodes().contains("COMP1701"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TS7.14 - prepareSwap returns failure when timetable does not exist")
    void ts714PrepareSwapFailsWhenTimetableNotFound() {
        PendingSwapResult result = service.prepareSwap("Nonexistent", "groupA", "groupB");
        assertFalse(result.canApply());
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TS7.15 - prepareSwap returns failure when existing group key is not in timetable")
    void ts715PrepareSwapFailsWhenGroupNotFound() {
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1, "Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))));
        service.generateTimetable(settings("Swap Test"));
        PendingSwapResult result = service.prepareSwap("Swap Test", "nonexistent-group", "another-group");
        assertFalse(result.canApply());
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("TS7.16 - applySwap saves the proposed timetable when canApply is true")
    void ts716ApplySwapSavesProposedTimetable() {
        Timetable proposed = new Timetable("Applied Swap");
        PendingSwapResult pending = new PendingSwapResult(true, false, proposed, "OK", List.of());
        service.applySwap(pending);
        assertTrue(repository.findTimetable("Applied Swap").isPresent());
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("TS7.17 - applySwap does nothing when result is null")
    void ts717ApplySwapDoesNothingWhenNull() {
        service.applySwap(null);
        assertTrue(repository.listTimetables().isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TS7.18 - Empty semester set is invalid")
    void ts718EmptySemesterSetIsInvalid() {
        TimetableSettings settings = settings("Empty Sem");
        settings.setSemesters(Set.of());
        ValidationResult result = service.validateSettings(settings);
        assertFalse(result.isValid());
    }
}
