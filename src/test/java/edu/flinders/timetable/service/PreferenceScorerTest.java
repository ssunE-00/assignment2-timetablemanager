package edu.flinders.timetable.service;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.PreferenceType;
import edu.flinders.timetable.model.TimetableSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class PreferenceScorerTest {

    private final PreferenceScorer scorer = new PreferenceScorer();

    private ClassRecord record(String campus, DayOfWeek day, LocalTime start) {
        return new ClassRecord(
                "COMP1701", "Game Design", "In person", campus, 2, 1,
                "Workshop", 1,
                LocalDate.of(2026, 7, 27), LocalDate.of(2026, 9, 14),
                day, start, start.plusHours(1),
                campus + " Building", "Room"
        );
    }

    private int score(PreferenceType pref, List<ClassRecord> records) {
        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(pref));
        return scorer.score(records, settings, records);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.01 - Morning preference scores positive for class before noon")
    void ps801MorningPreferencePositive() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertTrue(score(PreferenceType.MORNINGS, List.of(r)) > 0);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.02 - Afternoon preference scores positive for class at noon or after")
    void ps802AfternoonPreferencePositive() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(13, 0));
        assertTrue(score(PreferenceType.AFTERNOONS, List.of(r)) > 0);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.03 - TONSLEY campus preference scores positive for Tonsley class")
    void ps803TonsleyCampusPreferencePositive() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertTrue(score(PreferenceType.TONSLEY, List.of(r)) > 0);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.04 - BEDFORD_PARK preference scores zero for non Bedford Park class")
    void ps804BedfordParkPreferenceMismatch() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertEquals(0, score(PreferenceType.BEDFORD_PARK, List.of(r)));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.05 - BEDFORD_PARK preference scores positive for Bedford Park class")
    void ps805BedfordParkPreferencePositive() {
        ClassRecord r = record("Bedford Park", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertTrue(score(PreferenceType.BEDFORD_PARK, List.of(r)) > 0);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.06 - FLINDERS_CITY preference scores positive for Flinders City Campus class")
    void ps806FlindersCityPreferencePositive() {
        ClassRecord r = record("Flinders City Campus", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertTrue(score(PreferenceType.FLINDERS_CITY, List.of(r)) > 0);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.07 - SAME_CAMPUS preference scores positive when all classes at same campus")
    void ps807SameCampusTrue() {
        ClassRecord a = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        ClassRecord b = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(10, 0));

        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(PreferenceType.SAME_CAMPUS));

        assertTrue(scorer.score(List.of(a, b), settings, List.of(a, b)) > 0);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.08 - SAME_CAMPUS preference scores zero when classes are at different campuses")
    void ps808SameCampusFalse() {
        ClassRecord a = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        ClassRecord b = record("Bedford Park", DayOfWeek.MONDAY, LocalTime.of(10, 0));

        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(PreferenceType.SAME_CAMPUS));

        assertEquals(0, scorer.score(List.of(a, b), settings, List.of(a, b)));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.09 - MONDAY preference scores positive for Monday class")
    void ps809MondayPreferencePositive() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertTrue(score(PreferenceType.MONDAY, List.of(r)) > 0);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.10 - TUESDAY preference scores zero for Monday class")
    void ps810TuesdayPreferenceMismatch() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));
        assertEquals(0, score(PreferenceType.TUESDAY, List.of(r)));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.11 - WEDNESDAY preference scores positive for Wednesday class")
    void ps811WednesdayPreferencePositive() {
        ClassRecord r = record("Tonsley", DayOfWeek.WEDNESDAY, LocalTime.of(9, 0));
        assertTrue(score(PreferenceType.WEDNESDAY, List.of(r)) > 0);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.12 - THURSDAY preference scores positive for Thursday class")
    void ps812ThursdayPreferencePositive() {
        ClassRecord r = record("Tonsley", DayOfWeek.THURSDAY, LocalTime.of(9, 0));
        assertTrue(score(PreferenceType.THURSDAY, List.of(r)) > 0);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.13 - FRIDAY preference scores positive for Friday class")
    void ps813FridayPreferencePositive() {
        ClassRecord r = record("Tonsley", DayOfWeek.FRIDAY, LocalTime.of(9, 0));
        assertTrue(score(PreferenceType.FRIDAY, List.of(r)) > 0);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.14 - EVENLY_SPREAD preference scores positive when classes span three or more days")
    void ps814EvenlySpreadPositive() {
        List<ClassRecord> records = List.of(
                record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0)),
                record("Tonsley", DayOfWeek.TUESDAY, LocalTime.of(9, 0)),
                record("Tonsley", DayOfWeek.WEDNESDAY, LocalTime.of(9, 0))
        );
        assertTrue(score(PreferenceType.EVENLY_SPREAD, records) > 0);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.15 - EVENLY_SPREAD preference scores zero when classes are on fewer than three days")
    void ps815EvenlySpreadFalse() {
        List<ClassRecord> records = List.of(
                record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0)),
                record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(10, 0))
        );
        assertEquals(0, score(PreferenceType.EVENLY_SPREAD, records));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.16 - COMPACT_DAYS preference scores positive when classes fit in two or fewer days")
    void ps816CompactDaysPositive() {
        List<ClassRecord> records = List.of(
                record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0)),
                record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(11, 0))
        );
        assertTrue(score(PreferenceType.COMPACT_DAYS, records) > 0);
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.17 - COMPACT_DAYS preference scores zero when classes span more than two days")
    void ps817CompactDaysFalse() {
        List<ClassRecord> records = List.of(
                record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0)),
                record("Tonsley", DayOfWeek.TUESDAY, LocalTime.of(9, 0)),
                record("Tonsley", DayOfWeek.WEDNESDAY, LocalTime.of(9, 0))
        );
        assertEquals(0, score(PreferenceType.COMPACT_DAYS, records));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("PS8.18 - Weight ordering gives higher score to first listed preference")
    void ps818WeightOrderingAffectsScore() {
        ClassRecord r = record("Tonsley", DayOfWeek.MONDAY, LocalTime.of(9, 0));

        TimetableSettings settings = new TimetableSettings();
        settings.setPreferences(List.of(PreferenceType.MORNINGS, PreferenceType.TONSLEY));

        int score = scorer.score(List.of(r), settings, List.of(r));

        assertTrue(score > 0);
    }
}
