package edu.flinders.timetable.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class TimeUtilTest {

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU12.01 - overlaps returns true when time ranges overlap")
    void tu1201OverlapsTrueForOverlappingRanges() {
        // 09:00-10:00 and 09:30-10:30 overlap.
        assertTrue(TimeUtil.overlaps(
                LocalTime.of(9, 0), LocalTime.of(10, 0),
                LocalTime.of(9, 30), LocalTime.of(10, 30)
        ));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU12.02 - overlaps returns false for back to back times")
    void tu1202OverlapsFalseForBackToBack() {
        // 09:00-10:00 ends exactly when 10:00-11:00 starts, so no overlap.
        assertFalse(TimeUtil.overlaps(
                LocalTime.of(9, 0), LocalTime.of(10, 0),
                LocalTime.of(10, 0), LocalTime.of(11, 0)
        ));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU12.03 - overlaps returns false when second range is entirely after first")
    void tu1203OverlapsFalseWhenSecondAfterFirst() {
        assertFalse(TimeUtil.overlaps(
                LocalTime.of(9, 0), LocalTime.of(10, 0),
                LocalTime.of(11, 0), LocalTime.of(12, 0)
        ));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU12.04 - overlaps returns false when second range is entirely before first")
    void tu1204OverlapsFalseWhenSecondBeforeFirst() {
        assertFalse(TimeUtil.overlaps(
                LocalTime.of(11, 0), LocalTime.of(12, 0),
                LocalTime.of(9, 0), LocalTime.of(10, 0)
        ));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU12.05 - minutesBetween returns correct positive gap")
    void tu1205MinutesBetweenReturnsPositiveGap() {
        assertEquals(30, TimeUtil.minutesBetween(LocalTime.of(10, 0), LocalTime.of(10, 30)));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU12.06 - minutesBetween returns zero when times are the same")
    void tu1206MinutesBetweenReturnsZeroWhenEqual() {
        assertEquals(0, TimeUtil.minutesBetween(LocalTime.of(10, 0), LocalTime.of(10, 0)));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("TU12.07 - minutesBetween returns negative when second time is before first")
    void tu1207MinutesBetweenReturnsNegativeWhenSecondBeforeFirst() {
        assertTrue(TimeUtil.minutesBetween(LocalTime.of(10, 30), LocalTime.of(10, 0)) < 0);
    }
}
