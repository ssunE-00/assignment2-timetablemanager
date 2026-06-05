package edu.flinders.timetable.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ClassRecordTest {

    private ClassRecord record(String classFormat, int instance, LocalDate firstDate, DayOfWeek day, LocalTime start, LocalTime end, String building, String room) {
        // this helper creates a simple ClassRecord so the tests stay shorter and easier to read.
        return new ClassRecord(
                "COMP1701",
                "Game Design",
                "In person",
                "Tonsley",
                2,
                1,
                classFormat,
                instance,
                firstDate,
                firstDate.plusWeeks(7),
                day,
                start,
                end,
                building,
                room
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CR4.01 - Import key ignores time and location")
    void cr401ImportKeyIgnoresTimeAndLocation() {
        // this is the first record with the original time and location.
        ClassRecord first = record("Workshop", 1, LocalDate.of(2026, 7, 27), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "Old Building", "Old Room");

        // this is almost the same record, but time and location are different.
        ClassRecord second = record("Workshop", 1, LocalDate.of(2026, 7, 27), DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), "New Building", "New Room");

        // this checks that both records have the same import key, because imports should update time/location instead of duplicating.
        assertEquals(first.importKey(), second.importKey());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CR4.02 - Group key ignores date day time and location")
    void cr402GroupKeyIgnoresDateDayTimeAndLocation() {
        // this is the first class record in a group.
        ClassRecord first = record("Workshop", 1, LocalDate.of(2026, 7, 27), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "Building A", "Room A");

        // this record has the same topic/class/instance, but a different date, day, time, and location.
        ClassRecord second = record("Workshop", 1, LocalDate.of(2026, 10, 5), DayOfWeek.FRIDAY, LocalTime.of(12, 0), LocalTime.of(13, 0), "Building B", "Room B");

        // this checks that the records are in the same browse group, but they are not the exact same imported row.
        assertAll(
                () -> assertEquals(first.groupKey(), second.groupKey()),
                () -> assertNotEquals(first.importKey(), second.importKey())
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CR4.03 - Copy creates separate class record")
    void cr403CopyCreatesSeparateClassRecord() {
        // this creates the original class record.
        ClassRecord original = record("Tutorial", 2, LocalDate.of(2026, 7, 27), DayOfWeek.WEDNESDAY, LocalTime.of(11, 0), LocalTime.of(12, 0), "Tonsley T1", "1.08");

        // this makes a copy of the record.
        ClassRecord copy = original.copy();

        // this changes only the copy, not the original.
        copy.setRoom("Changed Room");

        // this checks that the copy is a different object and changing it did not change the original.
        assertAll(
                () -> assertNotSame(original, copy),
                () -> assertEquals("1.08", original.getRoom()),
                () -> assertEquals("Changed Room", copy.getRoom())
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("CR4.04 - Lecture class is detected")
    void cr404LectureClassIsDetected() {
        // this creates a class with "Lecture" in the class format.
        ClassRecord lecture = record("Online Lecture", 1, LocalDate.of(2026, 7, 27), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "Online", "Room");

        // this checks that the isLecture method recognises it as a lecture.
        assertTrue(lecture.isLecture());
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("CR4.05 - Display line contains important class details")
    void cr405DisplayLineContainsImportantClassDetails() {
        // this creates a normal class record.
        ClassRecord classRecord = record("Workshop", 1, LocalDate.of(2026, 7, 27), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "Tonsley T1", "1.08");

        // this creates the display text that would be shown to a user.
        String display = classRecord.displayLine();

        // this checks that the display text includes the main important details.
        assertAll(
                () -> assertTrue(display.contains("COMP1701")),
                () -> assertTrue(display.contains("Tonsley")),
                () -> assertTrue(display.contains("Workshop")),
                () -> assertTrue(display.contains("Tonsley T1"))
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("CR4.06 - Getters setters choice key equals hashcode and tostring")
    void cr406RemainingCoverage() {

        ClassRecord record = record(
                "Workshop",
                1,
                LocalDate.of(2026, 7, 27),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Building A",
                "Room A"
        );

        // setters
        record.setTopicCode("COMP2701");
        record.setTopicName("Software Engineering");
        record.setAttendanceMode("Online");
        record.setCampus("City");
        record.setSemester(1);
        record.setAvailabilityNumber(3);
        record.setClassFormat("Tutorial");
        record.setClassInstance(4);
        record.setFirstClassDate(LocalDate.of(2026, 8, 1));
        record.setLastClassDate(LocalDate.of(2026, 10, 1));
        record.setDay(DayOfWeek.FRIDAY);
        record.setStartTime(LocalTime.of(13, 0));
        record.setEndTime(LocalTime.of(14, 0));
        record.setBuilding("B1");
        record.setRoom("2.01");

        // getters
        assertAll(
                () -> assertEquals("COMP2701", record.getTopicCode()),
                () -> assertEquals("Software Engineering", record.getTopicName()),
                () -> assertEquals("Online", record.getAttendanceMode()),
                () -> assertEquals("City", record.getCampus()),
                () -> assertEquals(1, record.getSemester()),
                () -> assertEquals(3, record.getAvailabilityNumber()),
                () -> assertEquals("Tutorial", record.getClassFormat()),
                () -> assertEquals(4, record.getClassInstance()),
                () -> assertEquals(LocalDate.of(2026, 8, 1), record.getFirstClassDate()),
                () -> assertEquals(LocalDate.of(2026, 10, 1), record.getLastClassDate()),
                () -> assertEquals(DayOfWeek.FRIDAY, record.getDay()),
                () -> assertEquals(LocalTime.of(13, 0), record.getStartTime()),
                () -> assertEquals(LocalTime.of(14, 0), record.getEndTime()),
                () -> assertEquals("B1", record.getBuilding()),
                () -> assertEquals("2.01", record.getRoom())
        );

        // choiceKey
        assertEquals("COMP2701|Tutorial", record.choiceKey());

        // isLecture false branch
        assertTrue(!record.isLecture());

        // toString
        assertEquals(record.displayLine(), record.toString());

        // equals same object
        assertEquals(record, record);

        // equals non-ClassRecord
        assertNotEquals(record, "not a class record");

        // equals true branch
        ClassRecord same = record.copy();
        assertEquals(record, same);

        // hashCode consistency
        assertEquals(record.hashCode(), same.hashCode());

        // equals false branch
        ClassRecord different = record.copy();
        different.setClassInstance(99);

        assertNotEquals(record, different);
    }
}
