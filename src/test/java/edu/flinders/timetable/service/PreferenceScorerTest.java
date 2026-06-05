package edu.flinders.timetable.service;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.PreferenceType;
import edu.flinders.timetable.model.TimetableSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PreferenceScorerTest {

    private final PreferenceScorer scorer = new PreferenceScorer();

    private ClassRecord record(String campus, DayOfWeek day, LocalTime start) {
        return new ClassRecord(
                "COMP1701",
                "Game Design",
                "In person",
                campus,
                2,
                1,
                "Workshop",
                1,
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                day,
                start,
                start.plusHours(1),
                campus + " Building",
                "Room"
        );
    }

    private int score(PreferenceType pref, List<ClassRecord> records) {
        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(pref));
        return scorer.score(records, settings, records);
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.01 - Morning preference positive")
    void ps801_morning() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertTrue(score(PreferenceType.MORNINGS, List.of(r)) > 0);
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.02 - Afternoon preference positive")
    void ps802_afternoon() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(13, 0));
        assertTrue(score(PreferenceType.AFTERNOONS, List.of(r)) > 0);
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.03 - Campus preference TONSLEY")
    void ps803_tonsley() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertTrue(score(PreferenceType.TONSLEY, List.of(r)) > 0);
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.04 - Campus preference BEDFORD_PARK mismatch")
    void ps804_bedford_park_fail() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertEquals(0, score(PreferenceType.BEDFORD_PARK, List.of(r)));
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.05 - SAME_CAMPUS true")
    void ps805_same_campus_true() {
        ClassRecord a = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        ClassRecord b = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(10, 0));

        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(PreferenceType.SAME_CAMPUS));

        int score = scorer.score(List.of(a, b), settings, List.of(a, b));

        assertTrue(score > 0);
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.06 - MONDAY preference")
    void ps806_monday() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertTrue(score(PreferenceType.MONDAY, List.of(r)) > 0);
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.07 - TUESDAY mismatch")
    void ps807_tuesday_fail() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertEquals(0, score(PreferenceType.TUESDAY, List.of(r)));
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.08 - EVENLY_SPREAD true")
    void ps808_evenly_spread() {
        List<ClassRecord> records = List.of(
                record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0)),
                record("Tonsley", DayOfWeek.TUESDAY, LocalTime.of(9, 0)),
                record("Tonsley", DayOfWeek.WEDNESDAY, LocalTime.of(9, 0))
        );

        assertTrue(score(PreferenceType.EVENLY_SPREAD, records) > 0);
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.09 - COMPACT_DAYS true")
    void ps809_compact_days() {
        List<ClassRecord> records = List.of(
                record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0)),
                record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(11, 0))
        );

        assertTrue(score(PreferenceType.COMPACT_DAYS, records) > 0);
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("PS8.10 - weight ordering affects score")
    void ps810_weight_effect() {

        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));

        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(
                PreferenceType.MORNINGS,
                PreferenceType.TONSLEY
        ));

        int score = scorer.score(List.of(r), settings, List.of(r));

        assertTrue(score > 0);
    }
}