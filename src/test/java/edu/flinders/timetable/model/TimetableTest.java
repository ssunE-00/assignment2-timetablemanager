package edu.flinders.timetable.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class TimetableTest {

    private ClassRecord record(String topicCode, String classFormat, int instance) {
        return new ClassRecord(
                topicCode, "Game Design", "In person", "Tonsley", 2, 1,
                classFormat, instance,
                LocalDate.of(2026, 7, 27), LocalDate.of(2026, 9, 14),
                DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Tonsley T1", "1.08"
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TT13.01 - Single argument constructor sets name and creates empty record list")
    void tt1301ConstructorSetsNameAndEmptyRecords() {
        Timetable t = new Timetable("My Timetable");
        assertAll(
                () -> assertEquals("My Timetable", t.getName()),
                () -> assertTrue(t.getRecords().isEmpty()),
                () -> assertNotNull(t.getCreatedAt())
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TT13.02 - Two argument constructor adds supplied records")
    void tt1302TwoArgConstructorAddsRecords() {
        ClassRecord r = record("COMP1701", "Workshop", 1);
        Timetable t = new Timetable("Test", List.of(r));
        assertEquals(1, t.getRecords().size());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TT13.03 - setName changes the timetable name")
    void tt1303SetNameChangesName() {
        Timetable t = new Timetable("Old");
        t.setName("New");
        assertEquals("New", t.getName());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TT13.04 - addRecords appends records to an existing timetable")
    void tt1304AddRecordsAppendsRecords() {
        Timetable t = new Timetable("Test");
        t.addRecords(List.of(record("COMP1701", "Workshop", 1)));
        assertEquals(1, t.getRecords().size());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TT13.05 - removeGroup removes all records with the given group key")
    void tt1305RemoveGroupRemovesMatchingRecords() {
        ClassRecord r = record("COMP1701", "Workshop", 1);
        Timetable t = new Timetable("Test", List.of(r));
        t.removeGroup(r.groupKey());
        assertTrue(t.getRecords().isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TT13.06 - recordsByGroupKey groups records with the same group key together")
    void tt1306RecordsByGroupKeyGroupsCorrectly() {
        ClassRecord a = record("COMP1701", "Workshop", 1);
        ClassRecord b = record("COMP1701", "Workshop", 1);
        Timetable t = new Timetable("Test", List.of(a, b));
        Map<String, List<ClassRecord>> grouped = t.recordsByGroupKey();
        assertEquals(1, grouped.size());
        assertEquals(2, grouped.values().iterator().next().size());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TT13.07 - copy creates an independent timetable with the same name and records")
    void tt1307CopyCreatesIndependentTimetable() {
        ClassRecord r = record("COMP1701", "Workshop", 1);
        Timetable original = new Timetable("Original", List.of(r));
        Timetable copy = original.copy();
        copy.setName("Copy");
        assertAll(
                () -> assertEquals("Original", original.getName()),
                () -> assertEquals("Copy", copy.getName()),
                () -> assertNotSame(original, copy),
                () -> assertEquals(1, copy.getRecords().size())
        );
    }
}
