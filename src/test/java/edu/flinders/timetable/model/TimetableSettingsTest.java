package edu.flinders.timetable.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class TimetableSettingsTest {

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TS1.01 - Default constructor creates empty settings")
    void ts101DefaultConstructorCreatesEmptySettings() {
        TimetableSettings settings = new TimetableSettings();
        assertAll(
                () -> assertNull(settings.getTimetableName()),
                () -> assertTrue(settings.getSemesters().isEmpty()),
                () -> assertTrue(settings.getTopicCodes().isEmpty()),
                () -> assertTrue(settings.getCampuses().isEmpty()),
                () -> assertFalse(settings.isAllowLectureOverlap()),
                () -> assertTrue(settings.getPreferences().isEmpty())
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TS1.02 - Setters and getters work correctly")
    void ts102SettersAndGettersWork() {
        TimetableSettings settings = new TimetableSettings();
        settings.setTimetableName("My Timetable");
        settings.setSemesters(new LinkedHashSet<>(Set.of(1, 2)));
        settings.setTopicCodes(new LinkedHashSet<>(Set.of("COMP1701", "COMP2701")));
        settings.setCampuses(new LinkedHashSet<>(Set.of("Tonsley")));
        settings.setAllowLectureOverlap(true);
        settings.setPreferences(List.of(PreferenceType.MORNINGS));

        assertAll(
                () -> assertEquals("My Timetable", settings.getTimetableName()),
                () -> assertTrue(settings.getSemesters().contains(1)),
                () -> assertTrue(settings.getSemesters().contains(2)),
                () -> assertTrue(settings.getTopicCodes().contains("COMP1701")),
                () -> assertTrue(settings.getCampuses().contains("Tonsley")),
                () -> assertTrue(settings.isAllowLectureOverlap()),
                () -> assertEquals(1, settings.getPreferences().size())
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TS1.03 - Copy creates independent settings object")
    void ts103CopyCreatesIndependentSettingsObject() {
        TimetableSettings original = new TimetableSettings();
        original.setTimetableName("Original");
        original.getSemesters().add(2);
        original.getTopicCodes().add("COMP1701");
        original.getCampuses().add("Tonsley");
        original.setAllowLectureOverlap(false);
        original.setPreferences(List.of(PreferenceType.MORNINGS));

        TimetableSettings copy = original.copy();
        copy.setTimetableName("Copy");
        copy.getSemesters().add(1);

        assertAll(
                () -> assertEquals("Original", original.getTimetableName()),
                () -> assertEquals("Copy", copy.getTimetableName()),
                () -> assertFalse(original.getSemesters().contains(1)),
                () -> assertTrue(copy.getSemesters().contains(1))
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("TS1.04 - Copy preserves all field values")
    void ts104CopyPreservesAllFields() {
        TimetableSettings original = new TimetableSettings();
        original.setTimetableName("Test");
        original.setSemesters(new LinkedHashSet<>(Set.of(2)));
        original.setTopicCodes(new LinkedHashSet<>(Set.of("COMP1701")));
        original.setCampuses(new LinkedHashSet<>(Set.of("Tonsley")));
        original.setAllowLectureOverlap(true);
        original.setPreferences(List.of(PreferenceType.MORNINGS, PreferenceType.TONSLEY));

        TimetableSettings copy = original.copy();

        assertAll(
                () -> assertEquals("Test", copy.getTimetableName()),
                () -> assertEquals(Set.of(2), copy.getSemesters()),
                () -> assertEquals(Set.of("COMP1701"), copy.getTopicCodes()),
                () -> assertEquals(Set.of("Tonsley"), copy.getCampuses()),
                () -> assertTrue(copy.isAllowLectureOverlap()),
                () -> assertEquals(2, copy.getPreferences().size())
        );
    }
}
