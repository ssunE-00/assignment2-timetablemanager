package edu.flinders.timetable.service;

import edu.flinders.timetable.data.FileExporter;
import edu.flinders.timetable.model.Timetable;

import java.nio.file.Path;

public class ExportService {
    private final FileExporter fileExporter;

    public ExportService(FileExporter fileExporter) {
        this.fileExporter = fileExporter;
    }

    public Path exportTimetable(Timetable timetable, Path outputPath) {
        return fileExporter.exportTimetableToCsv(timetable, outputPath);
    }
}
