package edu.flinders.timetable.result;

import edu.flinders.timetable.model.ClassRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ScheduleWarningTest {

    @Test
    @Tag("hone0038")
    @Tag("Core")
    @DisplayName("TW1.01 - Create TIME_CLASH warning and verify fields")
    void tw101CreateTimeClashWarning() {
        ClassRecord first = null;
        ClassRecord second = null;

        ScheduleWarning warning = new ScheduleWarning(
                ScheduleWarning.Type.TIME_CLASH,
                first,
                second,
                "Classes overlap in time"
        );

        assertAll(
                () -> assertEquals(ScheduleWarning.Type.TIME_CLASH, warning.getType()),
                () -> assertEquals(first, warning.getFirstClass()),
                () -> assertEquals(second, warning.getSecondClass()),
                () -> assertEquals("Classes overlap in time", warning.getMessage())
        );
    }

    @Test
    @Tag("hone0038")
    @Tag("Core")
    @DisplayName("TW1.02 - Create COMMUTE_GAP warning and verify fields")
    void tw102CreateCommuteGapWarning() {
        ClassRecord first = null;
        ClassRecord second = null;

        ScheduleWarning warning = new ScheduleWarning(
                ScheduleWarning.Type.COMMUTE_GAP,
                first,
                second,
                "Not enough travel time between classes"
        );

        assertAll(
                () -> assertEquals(ScheduleWarning.Type.COMMUTE_GAP, warning.getType()),
                () -> assertEquals(first, warning.getFirstClass()),
                () -> assertEquals(second, warning.getSecondClass()),
                () -> assertEquals("Not enough travel time between classes", warning.getMessage())
        );
    }

    @Test
    @Tag("hone0038")
    @Tag("Core")
    @DisplayName("TW1.03 - toString formats type and message correctly")
    void tw103ToStringFormat() {
        ScheduleWarning warning = new ScheduleWarning(
                ScheduleWarning.Type.TIME_CLASH,
                null,
                null,
                "Overlap detected"
        );

        assertEquals("TIME_CLASH: Overlap detected", warning.toString());
    }
}