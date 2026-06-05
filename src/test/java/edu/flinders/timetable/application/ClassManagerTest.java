package edu.flinders.timetable.application;

import edu.flinders.timetable.data.DataRepository;
import edu.flinders.timetable.model.ClassGroup;
import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.SearchCriteria;
import edu.flinders.timetable.service.ClassService;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ClassManagerTest {

    private ClassManager classManager;
    private DataRepository repository;

    @BeforeEach
    void setup() {
        repository = new DataRepository();
        ClassService service = new ClassService(repository);
        classManager = new ClassManager(service);
    }

    private ClassRecord record() {
        return new ClassRecord(
                "COMP1701", "Game Design", "In person", "Tonsley", 2, 1,
                "Workshop", 1,
                LocalDate.of(2026, 7, 27), LocalDate.of(2026, 9, 14),
                DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Tonsley T1", "1.08"
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CM1.01 - Browse classes returns non null list")
    void cm101BrowseClasses() {
        List<ClassGroup> result = classManager.browseClasses();
        assertNotNull(result);
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CM1.02 - View classes returns non null list")
    void cm102ViewClasses() {
        List<ClassRecord> result = classManager.viewClasses();
        assertNotNull(result);
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CM1.03 - Search classes returns non null list")
    void cm103SearchClasses() {
        SearchCriteria criteria = new SearchCriteria();
        List<ClassRecord> result = classManager.searchClasses(criteria);
        assertNotNull(result);
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("CM1.04 - Find class returns empty optional when key not present")
    void cm104FindClass() {
        Optional<ClassRecord> result = classManager.findClass("nonexistent-KEY");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("CM1.05 - Edit class returns false when key does not exist")
    void cm105EditClass() {
        boolean result = classManager.editClass("nonexistent-KEY", record());
        assertFalse(result);
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("CM1.06 - Delete class returns false when key does not exist")
    void cm106DeleteClass() {
        boolean result = classManager.deleteClass("nonexistent-KEY");
        assertFalse(result);
    }
}
