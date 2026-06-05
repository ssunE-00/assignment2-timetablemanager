package edu.flinders.timetable.service;

import edu.flinders.timetable.data.FileExporter;
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
class ExportServiceTest {

    @TempDir
    Path tempDir;

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
    @DisplayName("ES10.01 - Export service creates a CSV file at the specified path")
    void es1001ExportServiceCreatesCsvFile() throws Exception {
        ExportService service = new ExportService(new FileExporter());
        Timetable timetable = new Timetable("Test Export", List.of(record()));
        Path output = tempDir.resolve("output.csv");

        Path result = service.exportTimetable(timetable, output);

        assertTrue(Files.exists(result));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("ES10.02 - Export service returns the output path")
    void es1002ExportServiceReturnsOutputPath() {
        ExportService service = new ExportService(new FileExporter());
        Timetable timetable = new Timetable("Test", List.of(record()));
        Path output = tempDir.resolve("out.csv");

        Path returned = service.exportTimetable(timetable, output);

        assertEquals(output, returned);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("ES10.03 - Exported CSV contains correct header and record data")
    void es1003ExportedCsvContainsCorrectHeaderAndData() throws Exception {
        ExportService service = new ExportService(new FileExporter());
        Timetable timetable = new Timetable("Test", List.of(record()));
        Path output = tempDir.resolve("timetable.csv");

        service.exportTimetable(timetable, output);
        List<String> lines = Files.readAllLines(output);

        assertAll(
                () -> assertEquals(2, lines.size()),
                () -> assertTrue(lines.get(0).contains("Topic Code")),
                () -> assertTrue(lines.get(1).contains("COMP1701"))
        );
    }
}
