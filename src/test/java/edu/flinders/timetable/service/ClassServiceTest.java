package edu.flinders.timetable.service;

import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.model.ClassGroup;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.SearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ClassServiceTest {

    private DataRepository repository;
    private ClassService service;

    @BeforeEach
    void setUp() {
        repository = new DataRepository();
        service = new ClassService(repository);
    }

    private ClassRecord record(String topicCode, String classFormat, int instance) {
        return new ClassRecord(
                topicCode, "Game Design", "In person", "Tonsley", 2, 1,
                classFormat, instance,
                LocalDate.of(2026, 7, 27), LocalDate.of(2026, 9, 14),
                DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Tonsley T1", "1.08"
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CS6.01 - Browse classes groups records by group key")
    void cs601BrowseClassesGroupsRecordsByGroupKey() {
        // importing two records that belong to the same group key should produce one group.
        repository.importRecords(List.of(
                record("COMP1701", "Workshop", 1),
                record("COMP1701", "Workshop", 1)
        ));
        List<ClassGroup> groups = service.browseClasses();
        assertEquals(1, groups.size());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CS6.02 - Browse classes returns empty list when repository is empty")
    void cs602BrowseClassesEmptyWhenNoRecords() {
        assertTrue(service.browseClasses().isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CS6.03 - View classes returns all records sorted by topic code")
    void cs603ViewClassesReturnsSortedRecords() {
        // COMP9999 was added first but COMP1701 should appear first in sorted output.
        repository.importRecords(List.of(
                record("COMP9999", "Workshop", 1),
                record("COMP1701", "Workshop", 1)
        ));
        List<ClassRecord> result = service.viewClasses();
        assertEquals(2, result.size());
        assertEquals("COMP1701", result.get(0).getTopicCode());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CS6.04 - Search classes with matching topic code returns results")
    void cs604SearchClassesMatchingCriteriaReturnsResults() {
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1)));
        SearchCriteria criteria = new SearchCriteria();
        criteria.setTopicCode("COMP1701");
        List<ClassRecord> result = service.searchClasses(criteria);
        assertEquals(1, result.size());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CS6.05 - Search classes with no match returns empty list")
    void cs605SearchClassesNoMatchReturnsEmpty() {
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1)));
        SearchCriteria criteria = new SearchCriteria();
        criteria.setTopicCode("COMP9999");
        assertTrue(service.searchClasses(criteria).isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("CS6.06 - Search classes with null criteria returns all records")
    void cs606SearchClassesNullCriteriaReturnsAll() {
        repository.importRecords(List.of(record("COMP1701", "Workshop", 1)));
        List<ClassRecord> result = service.searchClasses(null);
        assertEquals(1, result.size());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CS6.07 - Find class by import key returns present optional")
    void cs607FindClassByImportKeyReturnsPresentOptional() {
        ClassRecord r = record("COMP1701", "Workshop", 1);
        repository.importRecords(List.of(r));
        Optional<ClassRecord> found = service.findClassByKey(r.importKey());
        assertTrue(found.isPresent());
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CS6.08 - Find class by missing key returns empty optional")
    void cs608FindClassByMissingKeyReturnsEmpty() {
        assertTrue(service.findClassByKey("no-such-key").isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("CS6.09 - Edit class replaces the record successfully")
    void cs609EditClassReplacesRecord() {
        ClassRecord original = record("COMP1701", "Workshop", 1);
        repository.importRecords(List.of(original));
        ClassRecord updated = record("COMP1701", "Tutorial", 2);
        boolean result = service.editClass(original.importKey(), updated);
        assertTrue(result);
        assertEquals("Tutorial", service.viewClasses().get(0).getClassFormat());
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("CS6.10 - Delete class removes the record from the repository")
    void cs610DeleteClassRemovesRecord() {
        ClassRecord r = record("COMP1701", "Workshop", 1);
        repository.importRecords(List.of(r));
        boolean deleted = service.deleteClass(r.importKey());
        assertTrue(deleted);
        assertTrue(service.viewClasses().isEmpty());
    }
}
