package edu.flinders.timetable.presentation;

import edu.flinders.timetable.application.ClassManager;
import edu.flinders.timetable.application.ImportManager;
import edu.flinders.timetable.application.TimetableManager;
import edu.flinders.timetable.data.CsvParser;
import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.data.CsvFormatException;
import edu.flinders.timetable.model.*;
import edu.flinders.timetable.result.*;
import edu.flinders.timetable.service.TimetableService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

class ConsoleUITest {

    private void setInput(String data) {
        InputStream in = new ByteArrayInputStream(data.getBytes());
        System.setIn(in);
    }

    @Test
    @Tag("hone0038")
    @DisplayName("UI1 - run basic menu flow safely")
    void ui_basic_flow() {

        ImportManager importManager = new ImportManager(
                new CsvParser() {
                    @Override
                    public List<ClassRecord> parse(Path path) {
                        return List.of();
                    }
                },
                new DataRepository() {
                    @Override
                    public ImportResult importRecords(List<ClassRecord> records) {
                        return new ImportResult();
                    }
                }
        );

        ClassManager classManager = new ClassManager(null) {
            @Override public List<ClassGroup> browseClasses() { return List.of(); }
            @Override public List<ClassRecord> viewClasses() { return List.of(); }
            @Override public List<ClassRecord> searchClasses(SearchCriteria criteria) { return List.of(); }
            @Override public Optional<ClassRecord> findClass(String key) { return Optional.empty(); }
            @Override public boolean editClass(String key, ClassRecord updated) { return true; }
            @Override public boolean deleteClass(String key) { return true; }
        };

        TimetableManager timetableManager = new TimetableManager(
                new TimetableService(null, null) {
                    @Override
                    public TimetableGenerationResult generateTimetable(TimetableSettings settings) {
                        return new TimetableGenerationResult(
                                true,
                                new Timetable("test", new ArrayList<>()),
                                "ok",
                                List.of(new ScheduleWarning(
                                        ScheduleWarning.Type.TIME_CLASH,
                                        null,
                                        null,
                                        "Overlap warning"
                                ))
                        );
                    }

                    @Override public List<Timetable> browseTimetables() { return List.of(); }
                    @Override public Optional<Timetable> viewTimetable(String name) { return Optional.of(new Timetable("test")); }
                    @Override public PendingSwapResult prepareSwap(String a, String b, String c) {
                        return new PendingSwapResult(true, false, null, "", List.of());
                    }
                    @Override public void applySwap(PendingSwapResult result) {}
                    @Override public boolean deleteTimetable(String name) { return true; }
                    @Override public TimetableSettings getLastSettings() { return new TimetableSettings(); }
                },
                new edu.flinders.timetable.service.ExportService(null) {
                    @Override
                    public Path exportTimetable(Timetable timetable, Path outputPath) {
                        return outputPath;
                    }
                }
        );

        setInput(
                "1\n" +
                        "dummy.csv\n" +
                        "8\n" +
                        "9\n" +
                        "test\n" +
                        "0\n"
        );

        new ConsoleUI(importManager, classManager, timetableManager).start();
    }

    @Test
    @Tag("hone0038")
    @DisplayName("UI2 - invalid menu option is handled")
    void ui_invalid_menu_option() {

        setInput("999\n0\n");

        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("hone0038")
    @DisplayName("UI3 - CSV import failure path (exception handling)")
    void ui_import_failure() {

        ImportManager importManager = new ImportManager(
                new CsvParser() {
                    @Override
                    public List<ClassRecord> parse(Path path) {
                        throw new CsvFormatException("bad csv");
                    }
                },
                new DataRepository() {
                    @Override
                    public ImportResult importRecords(List<ClassRecord> records) {
                        return new ImportResult();
                    }
                }
        );

        setInput("1\nfile.csv\n0\n");

        new ConsoleUI(importManager, dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("hone0038")
    @DisplayName("UI4 - browse and view empty class list")
    void ui_empty_class_views() {

        setInput("2\n3\n0\n");

        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("hone0038")
    @DisplayName("UI5 - browse empty timetables")
    void ui_empty_timetables() {

        setInput("8\n0\n");

        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("hone0038")
    @DisplayName("UI6 - view missing timetable")
    void ui_view_missing_timetable() {

        setInput("9\nunknown\n0\n");

        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("hone0038")
    @DisplayName("UI7 - export missing timetable")
    void ui_export_missing_timetable() {

        setInput("12\nmissing\n0\n");

        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("hone0038")
    @DisplayName("UI8 - delete timetable cancelled by user")
    void ui_delete_cancelled() {

        setInput("11\ntest\nn\n0\n");

        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    private ImportManager dummyImport() {
        return new ImportManager(
                new CsvParser() {
                    @Override public List<ClassRecord> parse(Path path) { return List.of(); }
                },
                new DataRepository() {
                    @Override public ImportResult importRecords(List<ClassRecord> records) {
                        return new ImportResult();
                    }
                }
        );
    }

    private ClassManager dummyClassManager() {
        return new ClassManager(null) {
            @Override public List<ClassGroup> browseClasses() { return List.of(); }
            @Override public List<ClassRecord> viewClasses() { return List.of(); }
            @Override public List<ClassRecord> searchClasses(SearchCriteria criteria) { return List.of(); }
            @Override public Optional<ClassRecord> findClass(String key) { return Optional.empty(); }
            @Override public boolean editClass(String key, ClassRecord updated) { return true; }
            @Override public boolean deleteClass(String key) { return true; }
        };
    }

    private TimetableManager dummyTimetableManager() {
        return new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings settings) {
                        return new TimetableGenerationResult(true, new Timetable("test", new ArrayList<>()), "ok", List.of());
                    }
                    @Override public List<Timetable> browseTimetables() { return List.of(); }
                    @Override public Optional<Timetable> viewTimetable(String name) { return Optional.empty(); }
                    @Override public PendingSwapResult prepareSwap(String a, String b, String c) {
                        return new PendingSwapResult(false, false, null, "fail", List.of());
                    }
                    @Override public void applySwap(PendingSwapResult result) {}
                    @Override public boolean deleteTimetable(String name) { return false; }
                    @Override public TimetableSettings getLastSettings() { return new TimetableSettings(); }
                },
                new edu.flinders.timetable.service.ExportService(null) {
                    @Override public Path exportTimetable(Timetable timetable, Path outputPath) {
                        return outputPath;
                    }
                }
        );
    }
}