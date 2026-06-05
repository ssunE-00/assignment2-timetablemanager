package edu.flinders.timetable.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class SearchCriteriaTest {

    private ClassRecord record() {
        // this helper creates one basic class record that the search criteria can test against.
        return new ClassRecord(
                "COMP1701",
                "Game Design",
                "In person",
                "Tonsley",
                2,
                1,
                "Workshop",
                1,
                LocalDate.of(2026, 7, 27),
                LocalDate.of(2026, 9, 14),
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Tonsley T1",
                "1.08"
        );
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("SC5.01 - Empty search criteria matches class")
    void sc501EmptySearchCriteriaMatchesClass() {
        // this creates empty search criteria, meaning the user has not filtered by anything.
        SearchCriteria criteria = new SearchCriteria();

        // this checks that empty criteria matches the class record.
        assertTrue(criteria.matches(record()));
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("SC5.02 - Topic code search is case insensitive")
    void sc502TopicCodeSearchIsCaseInsensitive() {
        // this creates search criteria with the topic code in lowercase.
        SearchCriteria criteria = new SearchCriteria();
        criteria.setTopicCode("comp1701");

        // this checks that lowercase still matches COMP1701.
        assertTrue(criteria.matches(record()));
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("SC5.03 - Multiple search criteria use AND logic")
    void sc503MultipleSearchCriteriaUseAndLogic() {
        // this creates criteria where all fields should match the record.
        SearchCriteria criteria = new SearchCriteria();
        criteria.setTopicCode("COMP1701");
        criteria.setCampus("Tonsley");
        criteria.setSemester(2);

        // this checks that the record matches when all selected fields are correct.
        assertTrue(criteria.matches(record()));
    }

    @Test
    @Tag("homv0001")
    @Tag("Core")
    @DisplayName("SC5.04 - Search criteria rejects mismatched semester")
    void sc504SearchCriteriaRejectsMismatchedSemester() {
        // this creates criteria where the topic code matches but the semester does not.
        SearchCriteria criteria = new SearchCriteria();
        criteria.setTopicCode("COMP1701");
        criteria.setSemester(1);

        // this checks that one wrong field causes the whole search match to fail.
        assertFalse(criteria.matches(record()));
    }

    @Test
    @Tag("homv0001")
    @Tag("Additional")
    @DisplayName("SC5.05 - All search fields can match")
    void sc505AllSearchFieldsCanMatch() {

        SearchCriteria criteria = new SearchCriteria();

        criteria.setTopicCode("COMP1701");
        criteria.setTopicName("Game");
        criteria.setAttendanceMode("person");
        criteria.setCampus("Tonsley");
        criteria.setSemester(2);
        criteria.setAvailabilityNumber(1);
        criteria.setClassFormat("Workshop");
        criteria.setClassInstance(1);
        criteria.setFirstClassDate(LocalDate.of(2026, 7, 27));
        criteria.setLastClassDate(LocalDate.of(2026, 9, 14));
        criteria.setDay(DayOfWeek.MONDAY);
        criteria.setStartTime(LocalTime.of(9, 0));
        criteria.setEndTime(LocalTime.of(10, 0));
        criteria.setBuilding("Tonsley");
        criteria.setRoom("1.08");

        assertTrue(criteria.matches(record()));
    }

    @Test
    @Tag("hone0338")
    @Tag("Additional")
    @DisplayName("SC5.07 - Getters and setters work")
    void sc507GettersAndSettersWork() {

        SearchCriteria criteria = new SearchCriteria();

        criteria.setTopicCode("COMP2701");
        criteria.setTopicName("Software Engineering");
        criteria.setAttendanceMode("Online");
        criteria.setCampus("City");
        criteria.setSemester(1);
        criteria.setAvailabilityNumber(3);
        criteria.setClassFormat("Tutorial");
        criteria.setClassInstance(4);
        criteria.setFirstClassDate(LocalDate.of(2026, 8, 1));
        criteria.setLastClassDate(LocalDate.of(2026, 10, 1));
        criteria.setDay(DayOfWeek.FRIDAY);
        criteria.setStartTime(LocalTime.of(13, 0));
        criteria.setEndTime(LocalTime.of(14, 0));
        criteria.setBuilding("B1");
        criteria.setRoom("2.01");

        assertTrue(
                criteria.getTopicCode().equals("COMP2701")
                        && criteria.getTopicName().equals("Software Engineering")
                        && criteria.getAttendanceMode().equals("Online")
                        && criteria.getCampus().equals("City")
                        && criteria.getSemester() == 1
                        && criteria.getAvailabilityNumber() == 3
                        && criteria.getClassFormat().equals("Tutorial")
                        && criteria.getClassInstance() == 4
                        && criteria.getFirstClassDate().equals(LocalDate.of(2026, 8, 1))
                        && criteria.getLastClassDate().equals(LocalDate.of(2026, 10, 1))
                        && criteria.getDay() == DayOfWeek.FRIDAY
                        && criteria.getStartTime().equals(LocalTime.of(13, 0))
                        && criteria.getEndTime().equals(LocalTime.of(14, 0))
                        && criteria.getBuilding().equals("B1")
                        && criteria.getRoom().equals("2.01")
        );
    }
}
