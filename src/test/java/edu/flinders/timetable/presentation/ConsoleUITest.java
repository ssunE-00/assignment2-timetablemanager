package edu.flinders.timetable.presentation;

import edu.flinders.timetable.application.ClassManager;
import edu.flinders.timetable.application.ImportManager;
import edu.flinders.timetable.application.TimetableManager;
import edu.flinders.timetable.data.CsvFormatException;
import edu.flinders.timetable.data.CsvParser;
import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.model.ClassGroup;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.SearchCriteria;
import edu.flinders.timetable.model.Timetable;
import edu.flinders.timetable.model.TimetableSettings;
import edu.flinders.timetable.result.ImportResult;
import edu.flinders.timetable.result.PendingSwapResult;
import edu.flinders.timetable.result.ScheduleWarning;
import edu.flinders.timetable.result.TimetableGenerationResult;
import edu.flinders.timetable.service.ExportService;
import edu.flinders.timetable.service.TimetableService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ConsoleUITest {

    private void setInput(String data) {
        InputStream in = new ByteArrayInputStream(data.getBytes());
        System.setIn(in);
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI1 - Basic menu flow runs without error")
    void ui1BasicMenuFlow() {
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
                                List.of(new ScheduleWarning(ScheduleWarning.Type.TIME_CLASH, null, null, "Overlap warning"))
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
                new ExportService(null) {
                    @Override
                    public Path exportTimetable(Timetable timetable, Path outputPath) {
                        return outputPath;
                    }
                }
        );

        setInput("1\ndummy.csv\n8\n9\ntest\n0\n");
        new ConsoleUI(importManager, classManager, timetableManager).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI2 - Invalid menu option is handled gracefully")
    void ui2InvalidMenuOptionHandled() {
        setInput("999\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("UI3 - CSV import failure is reported without crashing")
    void ui3CsvImportFailureHandled() {
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
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI4 - Browse and view empty class list shows no records message")
    void ui4EmptyClassViews() {
        setInput("2\n3\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI5 - Browse timetables with empty list shows no timetables message")
    void ui5EmptyTimetables() {
        setInput("8\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI6 - View missing timetable shows not found message")
    void ui6ViewMissingTimetable() {
        setInput("9\nunknown\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI7 - Export missing timetable shows not found message")
    void ui7ExportMissingTimetable() {
        setInput("12\nmissing\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI8 - Delete timetable cancelled by user does not delete")
    void ui8DeleteTimetableCancelled() {
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
                new ExportService(null) {
                    @Override public Path exportTimetable(Timetable timetable, Path outputPath) {
                        return outputPath;
                    }
                }
        );
    }
}
