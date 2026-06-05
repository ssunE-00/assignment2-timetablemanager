package edu.flinders.timetable.service;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.result.ScheduleWarning;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ScheduleServiceTest {

    private ClassRecord record(
            String topicCode, String campus, String classFormat,
            DayOfWeek day, LocalTime start, LocalTime end
    ) {
        return new ClassRecord(
                topicCode, "Test Topic", "In person", campus, 1, 1,
                classFormat, 1,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 1),
                day, start, end,
                campus + " Building", "R1"
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("SS1 - TIME_CLASH detected for overlapping classes on same day")
    void ss1TimeClashDetected() {
        ScheduleService service = new ScheduleService();

        ClassRecord a = record("COMP", "City", "Lecture", DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0));
        ClassRecord b = record("COMP", "City", "Lecture", DayOfWeek.MONDAY,
                LocalTime.of(9, 30), LocalTime.of(10, 30));

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertEquals(1, warnings.size());
        assertEquals(ScheduleWarning.Type.TIME_CLASH, warnings.get(0).getType());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("SS2 - No warning when classes are on different days")
    void ss2DifferentDaysNoWarning() {
        ScheduleService service = new ScheduleService();

        ClassRecord a = record("COMP", "City", "Lecture", DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0));
        ClassRecord b = record("COMP", "City", "Lecture", DayOfWeek.TUESDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0));

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertTrue(warnings.isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("SS3 - COMMUTE_GAP detected for insufficient travel time between campuses")
    void ss3CommuteGapDetected() {
        ScheduleService service = new ScheduleService();

        ClassRecord a = record("COMP", "City", "Tutorial", DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0));
        ClassRecord b = record("COMP", "Tonsley", "Tutorial", DayOfWeek.MONDAY,
                LocalTime.of(10, 10), LocalTime.of(11, 0));

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertEquals(1, warnings.size());
        assertEquals(ScheduleWarning.Type.COMMUTE_GAP, warnings.get(0).getType());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("SS4 - No commute warning when both classes are at the same campus")
    void ss4SameCampusNoCommuteWarning() {
        ScheduleService service = new ScheduleService();

        ClassRecord a = record("COMP", "City", "Tutorial", DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0));
        ClassRecord b = record("COMP", "City", "Tutorial", DayOfWeek.MONDAY,
                LocalTime.of(10, 10), LocalTime.of(11, 0));

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertTrue(warnings.isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("SS5 - Allow lecture overlap disables clash warning for lectures")
    void ss5AllowLectureOverlapDisablesWarning() {
        ScheduleService service = new ScheduleService();

        ClassRecord a = record("COMP", "City", "Lecture", DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0));
        ClassRecord b = record("COMP", "City", "Lecture", DayOfWeek.MONDAY,
                LocalTime.of(9, 30), LocalTime.of(10, 30));

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), true);

        assertTrue(warnings.isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("SS6 - No warning when gap between different campus classes is large enough")
    void ss6LargeGapNoWarning() {
        ScheduleService service = new ScheduleService();

        ClassRecord a = record("COMP", "City", "Tutorial", DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0));
        ClassRecord b = record("COMP", "Tonsley", "Tutorial", DayOfWeek.MONDAY,
                LocalTime.of(10, 50), LocalTime.of(11, 30));

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertTrue(warnings.isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("SS7 - No warning for empty record list")
    void ss7EmptyListNoWarning() {
        ScheduleService service = new ScheduleService();
        assertTrue(service.findWarnings(List.of(), false).isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("SS8 - TIME_CLASH is still flagged even with allowLectureOverlap when one class is not a lecture")
    void ss8TimeClashFlaggedWhenOnlyOneIsLecture() {
        ScheduleService service = new ScheduleService();

        ClassRecord lecture = record("COMP", "City", "Lecture", DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0));
        ClassRecord tutorial = record("COMP", "City", "Tutorial", DayOfWeek.MONDAY,
                LocalTime.of(9, 30), LocalTime.of(10, 30));

        // allow lecture overlap still skips the pair because one IS a lecture
        List<ScheduleWarning> warnings = service.findWarnings(List.of(lecture, tutorial), true);

        assertTrue(warnings.isEmpty());
    }
}
