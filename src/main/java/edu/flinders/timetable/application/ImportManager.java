package edu.flinders.timetable.application;

import edu.flinders.timetable.data.CsvParser;
import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.result.ImportResult;

import java.nio.file.Path;
import java.util.List;

public class ImportManager {
    private final CsvParser csvParser;
    private final DataRepository repository;

    public ImportManager(CsvParser csvParser, DataRepository repository) {
        this.csvParser = csvParser;
        this.repository = repository;
    }

    public ImportResult importCsv(Path path) {
        List<ClassRecord> records = csvParser.parse(path);
        return repository.importRecords(records);
    }
}
