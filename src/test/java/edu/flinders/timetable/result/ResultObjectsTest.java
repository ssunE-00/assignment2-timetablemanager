package edu.flinders.timetable.result;

import edu.flinders.timetable.model.Timetable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ResultObjectsTest {

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("RO9.01 - Import result records counts and errors")
    void ro901ImportResultRecordsCountsAndErrors() {
        // this creates an empty import result object.
        ImportResult result = new ImportResult();

        // this manually changes the result like the import process would.
        result.incrementNewRecordCount();
        result.incrementUpdatedRecordCount();
        result.addError("bad row");

        // this checks that the result object stores counts and errors properly.
        assertAll(
                () -> assertEquals(1, result.getNewRecordCount()),
                () -> assertEquals(1, result.getUpdatedRecordCount()),
                () -> assertTrue(result.hasErrors()),
                () -> assertEquals("bad row", result.getErrors().get(0))
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("RO9.02 - Timetable generation failure stores message")
    void ro902TimetableGenerationFailureStoresMessage() {
        // this creates a failed timetable generation result.
        TimetableGenerationResult result = TimetableGenerationResult.failure("No matching classes");

        // this checks that the failure result has no timetable and stores the error message.
        assertAll(
                () -> assertFalse(result.isSuccess()),
                () -> assertNull(result.getTimetable()),
                () -> assertEquals("No matching classes", result.getMessage())
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("RO9.03 - Timetable generation success stores timetable")
    void ro903TimetableGenerationSuccessStoresTimetable() {
        // this creates a simple timetable.
        Timetable timetable = new Timetable("Success Test");

        // this creates a successful generation result using that timetable.
        TimetableGenerationResult result = TimetableGenerationResult.success(timetable, List.of());

        // this checks that the success result stores the timetable and has no warnings.
        assertAll(
                () -> assertTrue(result.isSuccess()),
                () -> assertEquals(timetable, result.getTimetable()),
                () -> assertTrue(result.getWarnings().isEmpty())
        );
    }
}
