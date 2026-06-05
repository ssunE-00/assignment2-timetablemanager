package edu.flinders.timetable.application;

import edu.flinders.timetable.model.Timetable;
import edu.flinders.timetable.model.TimetableSettings;
import edu.flinders.timetable.result.PendingSwapResult;
import edu.flinders.timetable.result.TimetableGenerationResult;
import edu.flinders.timetable.service.ExportService;
import edu.flinders.timetable.service.TimetableService;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class TimetableManager {
    private final TimetableService timetableService;
    private final ExportService exportService;

    public TimetableManager(TimetableService timetableService, ExportService exportService) {
        this.timetableService = timetableService;
        this.exportService = exportService;
    }

    public TimetableGenerationResult generateTimetable(TimetableSettings settings) {
        return timetableService.generateTimetable(settings);
    }

    public List<Timetable> browseTimetables() {
        return timetableService.browseTimetables();
    }

    public Optional<Timetable> viewTimetable(String name) {
        return timetableService.viewTimetable(name);
    }

    public PendingSwapResult prepareSwap(String timetableName, String existingGroupKey, String replacementGroupKey) {
        return timetableService.prepareSwap(timetableName, existingGroupKey, replacementGroupKey);
    }

    public void applySwap(PendingSwapResult result) {
        timetableService.applySwap(result);
    }

    public boolean deleteTimetable(String name) {
        return timetableService.deleteTimetable(name);
    }

    public TimetableSettings getLastSettings() {
        return timetableService.getLastSettings();
    }

    public Path exportTimetable(Timetable timetable, Path outputPath) {
        return exportService.exportTimetable(timetable, outputPath);
    }
}
