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

import static org.junit.jupiter.api.Assertions.*;

class TimetableManagerTest {

    @Test
    @Tag("hone0038")
    @DisplayName("TM1.01 - generate timetable delegates")
    void tm101_generateTimetable() {

        TimetableSettings settings = new TimetableSettings();

        // FAKE SERVICE (replace constructor args with whatever your project uses)
        TimetableService service = new TimetableService(
                null,
                null
        ) {
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
    @Tag("hone0038")
    @DisplayName("TM1.02 - browse timetables")
    void tm102_browseTimetables() {

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
    @Tag("hone0038")
    @DisplayName("TM1.03 - view timetable optional")
    void tm103_viewTimetable() {

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
    @Tag("hone0038")
    @DisplayName("TM1.04 - swap flow")
    void tm104_swapFlow() {

        PendingSwapResult swap = new PendingSwapResult(
                true,          // canApply
                false,         // requiresConfirmation
                null,          // proposedTimetable
                "",            // message
                List.of()      // warnings
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
    @Tag("hone0038")
    @DisplayName("TM1.05 - delete timetable")
    void tm105_deleteTimetable() {

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
    @Tag("hone0038")
    @DisplayName("TM1.06 - export timetable")
    void tm106_exportTimetable() {

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
}