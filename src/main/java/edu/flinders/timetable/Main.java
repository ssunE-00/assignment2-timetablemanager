package edu.flinders.timetable;

import edu.flinders.timetable.application.ClassManager;
import edu.flinders.timetable.application.ImportManager;
import edu.flinders.timetable.application.TimetableManager;
import edu.flinders.timetable.data.CsvParser;
import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.data.FileExporter;
import edu.flinders.timetable.presentation.ConsoleUI;
import edu.flinders.timetable.service.ClassService;
import edu.flinders.timetable.service.ExportService;
import edu.flinders.timetable.service.ScheduleService;
import edu.flinders.timetable.service.TimetableService;

public class Main {
    public static void main(String[] args) {
        DataRepository repository = new DataRepository();
        CsvParser csvParser = new CsvParser();
        FileExporter fileExporter = new FileExporter();

        ScheduleService scheduleService = new ScheduleService();
        ClassService classService = new ClassService(repository);
        TimetableService timetableService = new TimetableService(repository, scheduleService);
        ExportService exportService = new ExportService(fileExporter);

        ImportManager importManager = new ImportManager(csvParser, repository);
        ClassManager classManager = new ClassManager(classService);
        TimetableManager timetableManager = new TimetableManager(timetableService, exportService);

        ConsoleUI consoleUI = new ConsoleUI(importManager, classManager, timetableManager);
        consoleUI.start();
    }
}
