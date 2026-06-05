package edu.flinders.timetable.application;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ClassManagerTest {

    private ClassManager classManager;

    @BeforeEach
    void setup() {
        // ⚠️ Replace this line with whatever your project uses to create ClassService
        ClassService service = new ClassService(null);

        classManager = new ClassManager(service);
    }

    @Test
    @Tag("hone0038")
    @Tag("Core")
    @DisplayName("CM1.01 - Browse classes returns list")
    void cm101BrowseClasses() {
        List<ClassGroup> result = classManager.browseClasses();
        assertNotNull(result);
    }

    @Test
    @Tag("hone0038")
    @Tag("Core")
    @DisplayName("CM1.02 - View classes returns list")
    void cm102ViewClasses() {
        List<ClassRecord> result = classManager.viewClasses();
        assertNotNull(result);
    }

    @Test
    @Tag("hone0038")
    @Tag("Core")
    @DisplayName("CM1.03 - Search classes returns list")
    void cm103SearchClasses() {
        SearchCriteria criteria = new SearchCriteria();
        List<ClassRecord> result = classManager.searchClasses(criteria);
        assertNotNull(result);
    }

    @Test
    @Tag("hone0038")
    @Tag("Core")
    @DisplayName("CM1.04 - Find class returns Optional")
    void cm104FindClass() {
        Optional<ClassRecord> result = classManager.findClass("KEY");
        assertNotNull(result);
    }

    @Test
    @Tag("hone0038")
    @Tag("Critical")
    @DisplayName("CM1.05 - Edit class returns boolean")
    void cm105EditClass() {
        assertDoesNotThrow(() ->
                classManager.editClass("KEY", null)
        );
    }

    @Test
    @Tag("hone0038")
    @Tag("Critical")
    @DisplayName("CM1.06 - Delete class returns boolean")
    void cm106DeleteClass() {
        assertDoesNotThrow(() ->
                classManager.deleteClass("KEY")
        );
    }
}