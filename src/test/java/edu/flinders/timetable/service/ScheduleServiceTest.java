package edu.flinders.timetable.service;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.result.ScheduleWarning;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleServiceTest {

    private ClassRecord record(
            String topicCode,
            String topicName,
            String attendanceMode,
            String campus,
            int semester,
            int availability,
            String classFormat,
            int instance,
            LocalDate firstDate,
            LocalDate lastDate,
            DayOfWeek day,
            LocalTime start,
            LocalTime end,
            String building,
            String room
    ) {
        return new ClassRecord(
                topicCode,
                topicName,
                attendanceMode,
                campus,
                semester,
                availability,
                classFormat,
                instance,
                firstDate,
                lastDate,
                day,
                start,
                end,
                building,
                room
        );
    }

    @Test
    @Tag("hone0038")
    @DisplayName("SS1 - TIME_CLASH detected")
    void ss1_time_clash() {

        ScheduleService service = new ScheduleService();

        ClassRecord a = record(
                "COMP", "A", "In person", "City", 1, 1,
                "Lecture", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R1"
        );

        ClassRecord b = record(
                "COMP", "B", "In person", "City", 1, 1,
                "Lecture", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 30),
                LocalTime.of(10, 30),
                "B1", "R2"
        );

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertEquals(1, warnings.size());
        assertEquals(ScheduleWarning.Type.TIME_CLASH, warnings.get(0).getType());
    }

    @Test
    @Tag("hone0038")
    @DisplayName("SS2 - different days no warning")
    void ss2_different_days() {

        ScheduleService service = new ScheduleService();

        ClassRecord a = record(
                "COMP", "A", "In person", "City", 1, 1,
                "Lecture", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R1"
        );

        ClassRecord b = record(
                "COMP", "B", "In person", "City", 1, 1,
                "Lecture", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.TUESDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R2"
        );

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertTrue(warnings.isEmpty());
    }

    @Test
    @Tag("hone0038")
    @DisplayName("SS3 - COMMUTE_GAP detected")
    void ss3_commute_gap() {

        ScheduleService service = new ScheduleService();

        ClassRecord a = record(
                "COMP", "A", "In person", "City", 1, 1,
                "Tutorial", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R1"
        );

        ClassRecord b = record(
                "COMP", "B", "In person", "Tonsley", 1, 1,
                "Tutorial", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(10, 10),
                LocalTime.of(11, 0),
                "B2", "R2"
        );

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertEquals(1, warnings.size());
        assertEquals(ScheduleWarning.Type.COMMUTE_GAP, warnings.get(0).getType());
    }

    @Test
    @Tag("hone0038")
    @DisplayName("SS4 - same campus no commute warning")
    void ss4_same_campus() {

        ScheduleService service = new ScheduleService();

        ClassRecord a = record(
                "COMP", "A", "In person", "City", 1, 1,
                "Tutorial", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R1"
        );

        ClassRecord b = record(
                "COMP", "B", "In person", "City", 1, 1,
                "Tutorial", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(10, 10),
                LocalTime.of(11, 0),
                "B2", "R2"
        );

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertTrue(warnings.isEmpty());
    }

    @Test
    @Tag("hone0038")
    @DisplayName("SS5 - allow lecture overlap disables warning")
    void ss5_allow_lecture_overlap() {

        ScheduleService service = new ScheduleService();

        ClassRecord a = record(
                "COMP", "A", "In person", "City", 1, 1,
                "Lecture", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R1"
        );

        ClassRecord b = record(
                "COMP", "B", "In person", "City", 1, 1,
                "Lecture", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 30),
                LocalTime.of(10, 30),
                "B1", "R2"
        );

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), true);

        assertTrue(warnings.isEmpty());
    }

    @Test
    @Tag("hone0038")
    @DisplayName("SS6 - large gap no warning")
    void ss6_large_gap() {

        ScheduleService service = new ScheduleService();

        ClassRecord a = record(
                "COMP", "A", "In person", "City", 1, 1,
                "Tutorial", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "B1", "R1"
        );

        ClassRecord b = record(
                "COMP", "B", "In person", "Tonsley", 1, 1,
                "Tutorial", 1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                DayOfWeek.MONDAY,
                LocalTime.of(10, 50),
                LocalTime.of(11, 30),
                "B2", "R2"
        );

        List<ScheduleWarning> warnings = service.findWarnings(List.of(a, b), false);

        assertTrue(warnings.isEmpty());
    }
}