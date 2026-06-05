package edu.flinders.timetable.data;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.Timetable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class FileExporterTest {

    @TempDir
    Path tempDir;

    private ClassRecord record(String building, String room) {
        // this helper creates one simple class record to export.
        return new ClassRecord(
                "COMP1701",
                "Game Design",
                "In person",
                "Flinders City Campus",
                2,
                1,
                "Workshop",
                1,
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                building,
                room
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("EX3.01 - Export timetable creates CSV file")
    void ex301ExportTimetableCreatesCsvFile() throws Exception {
        // this creates a timetable with one class in it.
        Timetable timetable = new Timetable("Export Test", List.of(record("Festival Tower", "506 Computer Lab")));

        // this creates a temporary output path, so the test does not write into the real project folders.
        Path outputPath = tempDir.resolve("exports").resolve("timetable.csv");

        // this exports the timetable to a CSV file.
        Path exported = new FileExporter().exportTimetableToCsv(timetable, outputPath);

        // this reads the exported CSV file back into Java.
        List<String> lines = Files.readAllLines(exported);

        // this checks that the file exists, has a header and one data row, and contains the expected topic code.
        assertAll(
                () -> assertTrue(Files.exists(exported)),
                () -> assertEquals(2, lines.size()),
                () -> assertTrue(lines.get(0).contains("Topic Code")),
                () -> assertTrue(lines.get(1).contains("COMP1701"))
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("EX3.02 - Export quotes fields that contain commas")
    void ex302ExportQuotesFieldsThatContainCommas() throws Exception {
        // this creates a timetable where the building and room both contain commas.
        Timetable timetable = new Timetable("Export Test", List.of(record("Festival Tower, Level 5", "506, Computer Lab")));

        // this creates a temporary file path for the exported CSV.
        Path outputPath = tempDir.resolve("timetable.csv");

        // this exports the timetable.
        Path exported = new FileExporter().exportTimetableToCsv(timetable, outputPath);

        // this reads the exported file so we can check the CSV text.
        List<String> lines = Files.readAllLines(exported);

        // this checks that comma fields were wrapped in quotes, otherwise the CSV columns would break.
        assertAll(
                () -> assertTrue(lines.get(1).contains("\"Festival Tower, Level 5\"")),
                () -> assertTrue(lines.get(1).contains("\"506, Computer Lab\""))
        );
    }
}
