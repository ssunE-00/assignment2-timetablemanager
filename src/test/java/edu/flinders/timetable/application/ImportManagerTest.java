package edu.flinders.timetable.application;

import edu.flinders.timetable.data.CsvParser;
import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.result.ImportResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImportManagerTest {

    private ClassRecord record() {
        return new ClassRecord(
                "COMP1701",
                "Game Design",
                "In person",
                "Tonsley",
                2,
                1,
                "Workshop",
                1,
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Tonsley T1",
                "1.08"
        );
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IM1.01 - importCsv processes records successfully")
    void im101_importCsvProcessesRecordsSuccessfully() {

        Path dummyPath = Paths.get("dummy.csv");

        CsvParser parser = new CsvParser() {
            @Override
            public List<ClassRecord> parse(Path path) {
                assertEquals(dummyPath, path);
                return List.of(record());
            }
        };

        DataRepository repository = new DataRepository() {
            @Override
            public ImportResult importRecords(List<ClassRecord> records) {
                assertEquals(1, records.size());
                return new ImportResult();
            }
        };

        ImportManager manager = new ImportManager(parser, repository);

        ImportResult result = manager.importCsv(dummyPath);

        assertNotNull(result);
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IM1.02 - importCsv handles empty CSV results")
    void im102_importCsvHandlesEmptyCsvResults() {

        Path dummyPath = Paths.get("empty.csv");

        CsvParser parser = new CsvParser() {
            @Override
            public List<ClassRecord> parse(Path path) {
                assertEquals(dummyPath, path);
                return List.of();
            }
        };

        DataRepository repository = new DataRepository() {
            @Override
            public ImportResult importRecords(List<ClassRecord> records) {
                assertTrue(records.isEmpty());
                return new ImportResult();
            }
        };

        ImportManager manager = new ImportManager(parser, repository);

        ImportResult result = manager.importCsv(dummyPath);

        assertNotNull(result);
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IM1.03 - constructor wiring smoke test")
    void im103_constructorWorks() {

        CsvParser parser = new CsvParser() {
            @Override
            public List<ClassRecord> parse(Path path) {
                return List.of();
            }
        };

        DataRepository repository = new DataRepository() {
            @Override
            public ImportResult importRecords(List<ClassRecord> records) {
                return new ImportResult();
            }
        };

        ImportManager manager = new ImportManager(parser, repository);

        assertNotNull(manager.importCsv(Paths.get("test.csv")));
    }
}