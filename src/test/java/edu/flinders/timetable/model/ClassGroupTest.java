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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ClassGroupTest {

    private ClassRecord record() {
        return new ClassRecord(
                "COMP1701", "Game Design", "In person", "Tonsley", 2, 1,
                "Workshop", 1,
                LocalDate.of(2026, 7, 27), LocalDate.of(2026, 9, 14),
                DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Tonsley T1", "1.08"
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CG14.01 - getKey returns the key passed to the constructor")
    void cg1401GetKeyReturnsConstructorKey() {
        ClassGroup group = new ClassGroup("myKey", "myName");
        assertEquals("myKey", group.getKey());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CG14.02 - getRecords is initially empty before any addRecord calls")
    void cg1402GetRecordsInitiallyEmpty() {
        ClassGroup group = new ClassGroup("key1", "name1");
        assertTrue(group.getRecords().isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CG14.03 - addRecord adds the record and getRecords returns it")
    void cg1403AddRecordAndGetRecords() {
        ClassGroup group = new ClassGroup("key1", "name1");
        ClassRecord r = record();
        group.addRecord(r);
        assertAll(
                () -> assertEquals(1, group.getRecords().size()),
                () -> assertSame(r, group.getRecords().get(0))
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CG14.04 - firstRecord returns the first added record")
    void cg1404FirstRecordReturnsFirstRecord() {
        ClassGroup group = new ClassGroup("key1", "name1");
        ClassRecord r = record();
        group.addRecord(r);
        assertSame(r, group.firstRecord());
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("CG14.05 - firstRecord throws IllegalStateException when group is empty")
    void cg1405FirstRecordThrowsWhenEmpty() {
        ClassGroup group = new ClassGroup("key1", "name1");
        assertThrows(IllegalStateException.class, group::firstRecord);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("CG14.06 - summary contains topic code campus and record count")
    void cg1406SummaryContainsKeyDetails() {
        ClassGroup group = new ClassGroup("key1", "name1");
        group.addRecord(record());
        String summary = group.summary();
        assertAll(
                () -> assertTrue(summary.contains("COMP1701")),
                () -> assertTrue(summary.contains("Tonsley")),
                () -> assertTrue(summary.contains("1 date record"))
        );
    }
}
