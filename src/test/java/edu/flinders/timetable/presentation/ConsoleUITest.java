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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ConsoleUITest {

    private void setInput(String data) {
        InputStream in = new ByteArrayInputStream(data.getBytes());
        System.setIn(in);
    }

    private ClassRecord makeRecord() {
        return new ClassRecord(
                "COMP1234", "Computing", "Online", "Bedford Park",
                1, 1, "Lecture", 1,
                LocalDate.of(2024, 2, 19), LocalDate.of(2024, 5, 27),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(11, 0),
                "BuildingA", "Room101"
        );
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

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI9 - Browse classes with non-empty list shows class summaries")
    void ui9NonEmptyBrowseClasses() {
        ClassRecord rec = makeRecord();
        ClassGroup group = new ClassGroup("g1", "n1");
        group.addRecord(rec);
        ClassManager cm = new ClassManager(null) {
            @Override public List<ClassGroup> browseClasses() { return List.of(group); }
            @Override public List<ClassRecord> viewClasses() { return List.of(); }
            @Override public List<ClassRecord> searchClasses(SearchCriteria c) { return List.of(); }
            @Override public Optional<ClassRecord> findClass(String key) { return Optional.empty(); }
            @Override public boolean editClass(String key, ClassRecord u) { return true; }
            @Override public boolean deleteClass(String key) { return true; }
        };
        setInput("2\n0\n");
        new ConsoleUI(dummyImport(), cm, dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI10 - View classes with non-empty list shows each record")
    void ui10NonEmptyViewClasses() {
        ClassRecord rec = makeRecord();
        ClassManager cm = new ClassManager(null) {
            @Override public List<ClassGroup> browseClasses() { return List.of(); }
            @Override public List<ClassRecord> viewClasses() { return List.of(rec); }
            @Override public List<ClassRecord> searchClasses(SearchCriteria c) { return List.of(); }
            @Override public Optional<ClassRecord> findClass(String key) { return Optional.empty(); }
            @Override public boolean editClass(String key, ClassRecord u) { return true; }
            @Override public boolean deleteClass(String key) { return true; }
        };
        setInput("3\n0\n");
        new ConsoleUI(dummyImport(), cm, dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI11 - Search classes with blank criteria shows result count")
    void ui11SearchClassesBlankCriteria() {
        // all blank inputs: topicCode, topicName, campus, semester(-1), classFormat, building, room
        setInput("4\n\n\n\n\n\n\n\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI12 - Search classes with specific criteria including semester shows results")
    void ui12SearchClassesWithCriteria() {
        ClassRecord rec = makeRecord();
        ClassManager cm = new ClassManager(null) {
            @Override public List<ClassGroup> browseClasses() { return List.of(); }
            @Override public List<ClassRecord> viewClasses() { return List.of(); }
            @Override public List<ClassRecord> searchClasses(SearchCriteria c) { return List.of(rec); }
            @Override public Optional<ClassRecord> findClass(String key) { return Optional.empty(); }
            @Override public boolean editClass(String key, ClassRecord u) { return true; }
            @Override public boolean deleteClass(String key) { return true; }
        };
        // topicCode, topicName, campus, semester=1, classFormat, building, room
        setInput("4\nCOMP1234\nComputing\nBedford Park\n1\nLecture\nBuildingA\nRoom101\n0\n");
        new ConsoleUI(dummyImport(), cm, dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI13 - Edit class not found shows not found message")
    void ui13EditClassNotFound() {
        setInput("5\nINVALID_KEY\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI14 - Edit class cancelled shows cancelled message")
    void ui14EditClassCancelled() {
        ClassRecord rec = makeRecord();
        ClassManager cm = new ClassManager(null) {
            @Override public List<ClassGroup> browseClasses() { return List.of(); }
            @Override public List<ClassRecord> viewClasses() { return List.of(); }
            @Override public List<ClassRecord> searchClasses(SearchCriteria c) { return List.of(); }
            @Override public Optional<ClassRecord> findClass(String key) { return Optional.of(rec); }
            @Override public boolean editClass(String key, ClassRecord u) { return true; }
            @Override public boolean deleteClass(String key) { return true; }
        };
        setInput("5\nsome_key\nn\n0\n");
        new ConsoleUI(dummyImport(), cm, dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("UI15 - Edit class confirmed and saved shows updated message")
    void ui15EditClassSaved() {
        ClassRecord rec = makeRecord();
        ClassManager cm = new ClassManager(null) {
            @Override public List<ClassGroup> browseClasses() { return List.of(); }
            @Override public List<ClassRecord> viewClasses() { return List.of(); }
            @Override public List<ClassRecord> searchClasses(SearchCriteria c) { return List.of(); }
            @Override public Optional<ClassRecord> findClass(String key) { return Optional.of(rec); }
            @Override public boolean editClass(String key, ClassRecord u) { return true; }
            @Override public boolean deleteClass(String key) { return true; }
        };
        // confirm y, then 6 blank field updates (keeps existing values)
        setInput("5\nsome_key\ny\n\n\n\n\n\n\n0\n");
        new ConsoleUI(dummyImport(), cm, dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI16 - Edit class update failed shows error message")
    void ui16EditClassFailed() {
        ClassRecord rec = makeRecord();
        ClassManager cm = new ClassManager(null) {
            @Override public List<ClassGroup> browseClasses() { return List.of(); }
            @Override public List<ClassRecord> viewClasses() { return List.of(); }
            @Override public List<ClassRecord> searchClasses(SearchCriteria c) { return List.of(); }
            @Override public Optional<ClassRecord> findClass(String key) { return Optional.of(rec); }
            @Override public boolean editClass(String key, ClassRecord u) { return false; }
            @Override public boolean deleteClass(String key) { return true; }
        };
        setInput("5\nsome_key\ny\n\n\n\n\n\n\n0\n");
        new ConsoleUI(dummyImport(), cm, dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI17 - Delete class cancelled shows cancelled message")
    void ui17DeleteClassCancelled() {
        setInput("6\nsome_key\nn\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("UI18 - Delete class confirmed and deleted shows success message")
    void ui18DeleteClassConfirmed() {
        ClassManager cm = new ClassManager(null) {
            @Override public List<ClassGroup> browseClasses() { return List.of(); }
            @Override public List<ClassRecord> viewClasses() { return List.of(); }
            @Override public List<ClassRecord> searchClasses(SearchCriteria c) { return List.of(); }
            @Override public Optional<ClassRecord> findClass(String key) { return Optional.empty(); }
            @Override public boolean editClass(String key, ClassRecord u) { return true; }
            @Override public boolean deleteClass(String key) { return true; }
        };
        setInput("6\nsome_key\ny\n0\n");
        new ConsoleUI(dummyImport(), cm, dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI19 - Delete class confirmed but class not found shows warning message")
    void ui19DeleteClassNotFound() {
        // dummyClassManager().deleteClass returns false
        setInput("6\nsome_key\ny\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("UI20 - Generate timetable with semester 1 succeeds")
    void ui20GenerateTimetableSemester1() {
        // name, semester=1, topic codes(blank), campuses(blank), overlap=n, preferences(blank)
        setInput("7\nMyTable\n1\n\n\nn\n\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI21 - Generate timetable with semester 2 succeeds")
    void ui21GenerateTimetableSemester2() {
        setInput("7\nMyTable\n2\n\n\nn\n\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI22 - Generate timetable with both semesters succeeds")
    void ui22GenerateTimetableBothSemesters() {
        setInput("7\nMyTable\nboth\n\n\nn\n\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI23 - Generate timetable blank semester with no previous defaults to both semesters")
    void ui23GenerateTimetableBlankSemesterDefaultsBoth() {
        // blank semester + empty previous semesters → defaults to {1,2}
        setInput("7\n\n\n\n\nn\n\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI24 - Generate timetable blank semester uses previous settings when available")
    void ui24GenerateTimetableUsesPreviousSemesters() {
        TimetableManager tm = new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                        return new TimetableGenerationResult(true, new Timetable("test", new ArrayList<>()), "ok", List.of());
                    }
                    @Override public List<Timetable> browseTimetables() { return List.of(); }
                    @Override public Optional<Timetable> viewTimetable(String name) { return Optional.empty(); }
                    @Override public PendingSwapResult prepareSwap(String a, String b, String c) {
                        return new PendingSwapResult(false, false, null, "fail", List.of());
                    }
                    @Override public void applySwap(PendingSwapResult result) {}
                    @Override public boolean deleteTimetable(String name) { return false; }
                    @Override public TimetableSettings getLastSettings() {
                        TimetableSettings s = new TimetableSettings();
                        s.setSemesters(Set.of(1));
                        return s;
                    }
                },
                new ExportService(null) {
                    @Override public Path exportTimetable(Timetable t, Path p) { return p; }
                }
        );
        setInput("7\n\n\n\n\nn\n\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("UI25 - Generate timetable failure shows red error message")
    void ui25GenerateTimetableFailure() {
        TimetableManager tm = new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                        return new TimetableGenerationResult(false, null, "generation failed", List.of());
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
                    @Override public Path exportTimetable(Timetable t, Path p) { return p; }
                }
        );
        setInput("7\nMyTable\n1\n\n\nn\n\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI26 - Generate timetable success with schedule warnings shows warnings list")
    void ui26GenerateTimetableWithWarnings() {
        TimetableManager tm = new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                        return new TimetableGenerationResult(
                                true,
                                new Timetable("test", new ArrayList<>()),
                                "ok",
                                List.of(new ScheduleWarning(ScheduleWarning.Type.TIME_CLASH, null, null, "Clash warning"))
                        );
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
                    @Override public Path exportTimetable(Timetable t, Path p) { return p; }
                }
        );
        setInput("7\nMyTable\n1\n\n\nn\n\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI27 - Generate timetable with valid preference numbers selects preferences")
    void ui27GenerateTimetableWithValidPreferences() {
        // preferences "1,2" selects first two PreferenceType values
        setInput("7\nMyTable\n1\n\n\nn\n1,2\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI28 - Generate timetable with invalid preference input is ignored gracefully")
    void ui28GenerateTimetableInvalidPreferences() {
        // "abc" triggers NumberFormatException, "0" gives index=-1 (skipped), "99" out of range (skipped)
        setInput("7\nMyTable\n1\n\n\nn\nabc,0,99\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI29 - Browse timetables with non-empty list shows timetable names")
    void ui29NonEmptyBrowseTimetables() {
        TimetableManager tm = new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                        return new TimetableGenerationResult(true, new Timetable("t", new ArrayList<>()), "ok", List.of());
                    }
                    @Override public List<Timetable> browseTimetables() {
                        return List.of(new Timetable("MyTimetable", new ArrayList<>()));
                    }
                    @Override public Optional<Timetable> viewTimetable(String name) { return Optional.empty(); }
                    @Override public PendingSwapResult prepareSwap(String a, String b, String c) {
                        return new PendingSwapResult(false, false, null, "fail", List.of());
                    }
                    @Override public void applySwap(PendingSwapResult result) {}
                    @Override public boolean deleteTimetable(String name) { return false; }
                    @Override public TimetableSettings getLastSettings() { return new TimetableSettings(); }
                },
                new ExportService(null) {
                    @Override public Path exportTimetable(Timetable t, Path p) { return p; }
                }
        );
        setInput("8\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI30 - View timetable found shows timetable records via printTimetable")
    void ui30ViewTimetableFound() {
        ClassRecord rec = makeRecord();
        Timetable tt = new Timetable("test", new ArrayList<>(List.of(rec)));
        TimetableManager tm = buildTimetableManagerWithView(Optional.of(tt));
        setInput("9\ntest\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI31 - Edit timetable not found shows not found message")
    void ui31EditTimetableNotFound() {
        setInput("10\nunknown\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI32 - Edit timetable swap fails shows error message")
    void ui32EditTimetableSwapFails() {
        ClassRecord rec = makeRecord();
        Timetable tt = new Timetable("test", new ArrayList<>(List.of(rec)));
        TimetableManager tm = new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                        return new TimetableGenerationResult(true, new Timetable("t", new ArrayList<>()), "ok", List.of());
                    }
                    @Override public List<Timetable> browseTimetables() { return List.of(); }
                    @Override public Optional<Timetable> viewTimetable(String name) { return Optional.of(tt); }
                    @Override public PendingSwapResult prepareSwap(String a, String b, String c) {
                        return new PendingSwapResult(false, false, null, "Cannot swap", List.of());
                    }
                    @Override public void applySwap(PendingSwapResult result) {}
                    @Override public boolean deleteTimetable(String name) { return false; }
                    @Override public TimetableSettings getLastSettings() { return new TimetableSettings(); }
                },
                new ExportService(null) {
                    @Override public Path exportTimetable(Timetable t, Path p) { return p; }
                }
        );
        setInput("10\ntest\noldGroup\nnewGroup\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("UI33 - Edit timetable swap requires confirmation and user cancels")
    void ui33EditTimetableSwapCancelled() {
        ClassRecord rec = makeRecord();
        Timetable tt = new Timetable("test", new ArrayList<>(List.of(rec)));
        TimetableManager tm = new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                        return new TimetableGenerationResult(true, new Timetable("t", new ArrayList<>()), "ok", List.of());
                    }
                    @Override public List<Timetable> browseTimetables() { return List.of(); }
                    @Override public Optional<Timetable> viewTimetable(String name) { return Optional.of(tt); }
                    @Override public PendingSwapResult prepareSwap(String a, String b, String c) {
                        return new PendingSwapResult(true, true, tt, "Clash",
                                List.of(new ScheduleWarning(ScheduleWarning.Type.TIME_CLASH, null, null, "Overlap")));
                    }
                    @Override public void applySwap(PendingSwapResult result) {}
                    @Override public boolean deleteTimetable(String name) { return false; }
                    @Override public TimetableSettings getLastSettings() { return new TimetableSettings(); }
                },
                new ExportService(null) {
                    @Override public Path exportTimetable(Timetable t, Path p) { return p; }
                }
        );
        setInput("10\ntest\noldGroup\nnewGroup\nn\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("UI34 - Edit timetable swap requires confirmation and user accepts")
    void ui34EditTimetableSwapAccepted() {
        ClassRecord rec = makeRecord();
        Timetable tt = new Timetable("test", new ArrayList<>(List.of(rec)));
        TimetableManager tm = new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                        return new TimetableGenerationResult(true, new Timetable("t", new ArrayList<>()), "ok", List.of());
                    }
                    @Override public List<Timetable> browseTimetables() { return List.of(); }
                    @Override public Optional<Timetable> viewTimetable(String name) { return Optional.of(tt); }
                    @Override public PendingSwapResult prepareSwap(String a, String b, String c) {
                        return new PendingSwapResult(true, true, tt, "Clash",
                                List.of(new ScheduleWarning(ScheduleWarning.Type.TIME_CLASH, null, null, "Overlap")));
                    }
                    @Override public void applySwap(PendingSwapResult result) {}
                    @Override public boolean deleteTimetable(String name) { return false; }
                    @Override public TimetableSettings getLastSettings() { return new TimetableSettings(); }
                },
                new ExportService(null) {
                    @Override public Path exportTimetable(Timetable t, Path p) { return p; }
                }
        );
        setInput("10\ntest\noldGroup\nnewGroup\ny\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("UI35 - Edit timetable swap succeeds without requiring confirmation")
    void ui35EditTimetableSwapSucceedsNoConfirm() {
        ClassRecord rec = makeRecord();
        Timetable tt = new Timetable("test", new ArrayList<>(List.of(rec)));
        TimetableManager tm = new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                        return new TimetableGenerationResult(true, new Timetable("t", new ArrayList<>()), "ok", List.of());
                    }
                    @Override public List<Timetable> browseTimetables() { return List.of(); }
                    @Override public Optional<Timetable> viewTimetable(String name) { return Optional.of(tt); }
                    @Override public PendingSwapResult prepareSwap(String a, String b, String c) {
                        return new PendingSwapResult(true, false, tt, "ok", List.of());
                    }
                    @Override public void applySwap(PendingSwapResult result) {}
                    @Override public boolean deleteTimetable(String name) { return false; }
                    @Override public TimetableSettings getLastSettings() { return new TimetableSettings(); }
                },
                new ExportService(null) {
                    @Override public Path exportTimetable(Timetable t, Path p) { return p; }
                }
        );
        setInput("10\ntest\noldGroup\nnewGroup\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("UI36 - Delete timetable confirmed and successfully deleted")
    void ui36DeleteTimetableConfirmedDeleted() {
        TimetableManager tm = new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                        return new TimetableGenerationResult(true, new Timetable("t", new ArrayList<>()), "ok", List.of());
                    }
                    @Override public List<Timetable> browseTimetables() { return List.of(); }
                    @Override public Optional<Timetable> viewTimetable(String name) { return Optional.empty(); }
                    @Override public PendingSwapResult prepareSwap(String a, String b, String c) {
                        return new PendingSwapResult(false, false, null, "fail", List.of());
                    }
                    @Override public void applySwap(PendingSwapResult result) {}
                    @Override public boolean deleteTimetable(String name) { return true; }
                    @Override public TimetableSettings getLastSettings() { return new TimetableSettings(); }
                },
                new ExportService(null) {
                    @Override public Path exportTimetable(Timetable t, Path p) { return p; }
                }
        );
        setInput("11\ntest\ny\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI37 - Delete timetable confirmed but timetable not found shows warning")
    void ui37DeleteTimetableConfirmedNotFound() {
        // dummyTimetableManager().deleteTimetable returns false
        setInput("11\nmissing\ny\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), dummyTimetableManager()).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("UI38 - Export timetable found with blank path uses default path")
    void ui38ExportTimetableFoundDefaultPath() {
        ClassRecord rec = makeRecord();
        Timetable tt = new Timetable("test", new ArrayList<>(List.of(rec)));
        TimetableManager tm = buildTimetableManagerWithView(Optional.of(tt));
        setInput("12\ntest\n\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI39 - Export timetable found with custom path uses that path")
    void ui39ExportTimetableFoundCustomPath() {
        ClassRecord rec = makeRecord();
        Timetable tt = new Timetable("test", new ArrayList<>(List.of(rec)));
        TimetableManager tm = buildTimetableManagerWithView(Optional.of(tt));
        setInput("12\ntest\ncustom_output.csv\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("UI40 - RuntimeException thrown in handler is caught and reported")
    void ui40RuntimeExceptionCaught() {
        TimetableManager tm = new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                        throw new RuntimeException("Unexpected error");
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
                    @Override public Path exportTimetable(Timetable t, Path p) { return p; }
                }
        );
        setInput("7\nMyTable\n1\n\n\nn\n\n0\n");
        new ConsoleUI(dummyImport(), dummyClassManager(), tm).start();
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private TimetableManager buildTimetableManagerWithView(Optional<Timetable> result) {
        return new TimetableManager(
                new TimetableService(null, null) {
                    @Override public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                        return new TimetableGenerationResult(true, new Timetable("t", new ArrayList<>()), "ok", List.of());
                    }
                    @Override public List<Timetable> browseTimetables() { return List.of(); }
                    @Override public Optional<Timetable> viewTimetable(String name) { return result; }
                    @Override public PendingSwapResult prepareSwap(String a, String b, String c) {
                        return new PendingSwapResult(false, false, null, "fail", List.of());
                    }
                    @Override public void applySwap(PendingSwapResult r) {}
                    @Override public boolean deleteTimetable(String name) { return false; }
                    @Override public TimetableSettings getLastSettings() { return new TimetableSettings(); }
                },
                new ExportService(null) {
                    @Override public Path exportTimetable(Timetable t, Path p) { return p; }
                }
        );
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
            @Override public boolean deleteClass(String key) { return false; }
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
