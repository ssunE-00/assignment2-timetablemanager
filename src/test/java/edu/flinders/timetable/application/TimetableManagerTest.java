package edu.flinders.timetable.application;

import edu.flinders.timetable.model.Timetable;
import edu.flinders.timetable.model.TimetableSettings;
import edu.flinders.timetable.result.PendingSwapResult;
import edu.flinders.timetable.result.TimetableGenerationResult;
import edu.flinders.timetable.service.ExportService;
import edu.flinders.timetable.service.TimetableService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimetableManagerTest {

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TM1.01 - generate timetable delegates to timetable service")
    void tm101GenerateTimetableDelegates() {

        TimetableSettings settings = new TimetableSettings();

        TimetableService service = new TimetableService(null, null) {
            @Override
            public TimetableGenerationResult generateTimetable(TimetableSettings s) {
                assertSame(settings, s);
                return null;
            }
        };

        ExportService exportService = new ExportService(null) {
            @Override
            public Path exportTimetable(Timetable timetable, Path outputPath) {
                return outputPath;
            }
        };

        TimetableManager manager = new TimetableManager(service, exportService);

        assertNull(manager.generateTimetable(settings));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TM1.02 - browse timetables returns the list from service")
    void tm102BrowseTimetables() {

        List<Timetable> list = List.of(new Timetable(null));

        TimetableService service = new TimetableService(null, null) {
            @Override
            public List<Timetable> browseTimetables() {
                return list;
            }
        };

        ExportService exportService = new ExportService(null) {
            @Override
            public Path exportTimetable(Timetable timetable, Path outputPath) {
                return outputPath;
            }
        };

        TimetableManager manager = new TimetableManager(service, exportService);

        assertEquals(list, manager.browseTimetables());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TM1.03 - view timetable returns optional from service")
    void tm103ViewTimetableReturnsOptional() {

        Timetable t = new Timetable(null);

        TimetableService service = new TimetableService(null, null) {
            @Override
            public Optional<Timetable> viewTimetable(String name) {
                return Optional.of(t);
            }
        };

        ExportService exportService = new ExportService(null) {
            @Override
            public Path exportTimetable(Timetable timetable, Path outputPath) {
                return outputPath;
            }
        };

        TimetableManager manager = new TimetableManager(service, exportService);

        assertTrue(manager.viewTimetable("x").isPresent());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TM1.04 - prepare and apply swap delegates correctly")
    void tm104PrepareAndApplySwapDelegates() {

        PendingSwapResult swap = new PendingSwapResult(
                true, false, null, "", List.of()
        );

        TimetableService service = new TimetableService(null, null) {
            @Override
            public PendingSwapResult prepareSwap(String a, String b, String c) {
                assertEquals("t1", a);
                assertEquals("g1", b);
                assertEquals("g2", c);
                return swap;
            }

            @Override
            public void applySwap(PendingSwapResult result) {
                assertSame(swap, result);
            }
        };

        ExportService exportService = new ExportService(null) {
            @Override
            public Path exportTimetable(Timetable timetable, Path outputPath) {
                return outputPath;
            }
        };

        TimetableManager manager = new TimetableManager(service, exportService);

        manager.applySwap(manager.prepareSwap("t1", "g1", "g2"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TM1.05 - delete timetable returns correct boolean from service")
    void tm105DeleteTimetableReturnsBooleanFromService() {

        TimetableService service = new TimetableService(null, null) {
            @Override
            public boolean deleteTimetable(String name) {
                return name.equals("test");
            }
        };

        ExportService exportService = new ExportService(null) {
            @Override
            public Path exportTimetable(Timetable timetable, Path outputPath) {
                return outputPath;
            }
        };

        TimetableManager manager = new TimetableManager(service, exportService);

        assertTrue(manager.deleteTimetable("test"));
        assertFalse(manager.deleteTimetable("nope"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TM1.06 - export timetable delegates to export service")
    void tm106ExportTimetableDelegates() {

        Path out = Paths.get("out.csv");
        Timetable timetable = new Timetable(null);

        TimetableService service = new TimetableService(null, null);

        ExportService exportService = new ExportService(null) {
            @Override
            public Path exportTimetable(Timetable t, Path outputPath) {
                assertSame(timetable, t);
                return out;
            }
        };

        TimetableManager manager = new TimetableManager(service, exportService);

        assertEquals(out, manager.exportTimetable(timetable, out));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("TM1.07 - getLastSettings delegates to timetable service")
    void tm107GetLastSettingsDelegates() {

        TimetableSettings expected = new TimetableSettings();

        TimetableService service = new TimetableService(null, null) {
            @Override
            public TimetableSettings getLastSettings() {
                return expected;
            }
        };

        TimetableManager manager = new TimetableManager(service, new ExportService(null));

        assertSame(expected, manager.getLastSettings());
    }
}
