package edu.flinders.timetable.data;

import edu.flinders.timetable.model.ClassRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class CsvParserTest {

    private final CsvParser parser = new CsvParser();

    private static final String HEADER = "Topic,Availability,Class,Class instance,Date,Day,Time,Location";

    private static String validRow() {
        return "COMP1701 Game Design,In person - Flinders City Campus - S2 - 1,Workshop-1,1,27 Jul - 14 Sep,Monday,09:00 - 10:00,Festival Tower, 506 Computer Lab";
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TV1.01 - Import classes from a .csv file")
    void tv101ImportClassesFromCsvFile() {
        Path csvFile = Path.of("examples", "sample-topic-data.csv");

        // this checks if the CSV parser crashes and/or cannot read the file. If it throws an exception, the test fails.
        List<ClassRecord> records = assertDoesNotThrow(() -> parser.parse(csvFile));

        // this checks if the parser creates 6 ClassRecord objects, since the sample CSV file has 6 class rows.
        assertEquals(6, records.size());
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TV1.02 - Parse location that contains a comma")
    void tv102ParseLocationThatContainsAComma() {
        // this creates a tiny CSV in memory, instead of needing a real file.
        List<String> lines = List.of(HEADER, validRow());

        // this runs the parser on the fake CSV lines.
        List<ClassRecord> records = parser.parseLines(lines);

        // this grabs the first class record so we can check what the parser created.
        ClassRecord record = records.get(0);

        // this checks that "Festival Tower, 506 Computer Lab" is split into building and room correctly.
        assertAll(
                () -> assertEquals("Festival Tower", record.getBuilding()),
                () -> assertEquals("506 Computer Lab", record.getRoom())
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TV1.03 - Parse topic and availability fields")
    void tv103ParseTopicAndAvailabilityFields() {
        // this creates a normal CSV row that should be accepted by the parser.
        List<String> lines = List.of(HEADER, validRow());

        // this parses the CSV row into a ClassRecord object.
        ClassRecord record = parser.parseLines(lines).get(0);

        // this checks the topic and availability information was broken up into the correct fields.
        assertAll(
                () -> assertEquals("COMP1701", record.getTopicCode()),
                () -> assertEquals("Game Design", record.getTopicName()),
                () -> assertEquals("In person", record.getAttendanceMode()),
                () -> assertEquals("Flinders City Campus", record.getCampus()),
                () -> assertEquals(2, record.getSemester()),
                () -> assertEquals(1, record.getAvailabilityNumber())
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TV1.04 - Parse date day and time fields")
    void tv104ParseDateDayAndTimeFields() {
        // this creates a normal CSV row that includes a date range, day, and time range.
        List<String> lines = List.of(HEADER, validRow());

        // this parses the CSV row into a ClassRecord object.
        ClassRecord record = parser.parseLines(lines).get(0);

        // this checks that the parser converted the text dates, day, and times into Java date/time objects.
        assertAll(
                () -> assertEquals(LocalDate.of(2026, 7, 27), record.getFirstClassDate()),
                () -> assertEquals(LocalDate.of(2026, 9, 14), record.getLastClassDate()),
                () -> assertEquals(DayOfWeek.MONDAY, record.getDay()),
                () -> assertEquals(LocalTime.of(9, 0), record.getStartTime()),
                () -> assertEquals(LocalTime.of(10, 0), record.getEndTime())
        );
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TV1.05 - Reject CSV without a data row")
    void tv105RejectCsvWithoutADataRow() {
        // this gives the parser only the header row and no actual class rows.
        List<String> lines = List.of(HEADER);

        // this checks that the parser rejects the CSV because there is no data to import.
        assertThrows(CsvFormatException.class, () -> parser.parseLines(lines));
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TV1.06 - Reject CSV with wrong header")
    void tv106RejectCsvWithWrongHeader() {
        // this header is wrong because it says Subject instead of Topic.
        String wrongHeader = "Subject,Availability,Class,Class instance,Date,Day,Time,Location";

        // this checks that the parser rejects a CSV file when the header format is not what the app expects.
        assertThrows(CsvFormatException.class, () -> parser.parseLines(List.of(wrongHeader, validRow())));
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TV1.07 - Reject class instance zero")
    void tv107RejectClassInstanceZero() {
        // this row uses class instance 0, which should be invalid because class instances should start from 1.
        String row = "COMP1701 Game Design,In person - Tonsley - S2 - 1,Workshop,0,27 Jul - 14 Sep,Monday,09:00 - 10:00,Tonsley T1, 1.08 Lecture Room";

        // this checks that the parser throws an error instead of accepting the bad class instance.
        assertThrows(CsvFormatException.class, () -> parser.parseLines(List.of(HEADER, row)));
    }

    @Test
    @Tag("kang0201")
    @Tag("Critical")
    @DisplayName("TV1.08 - Reject time range where start is after end")
    void tv108RejectTimeRangeWhereStartIsAfterEnd() {
        // this row has a start time of 12:00 and an end time of 10:00, which does not make sense.
        String row = "COMP1701 Game Design,In person - Tonsley - S2 - 1,Workshop,1,27 Jul - 14 Sep,Monday,12:00 - 10:00,Tonsley T1, 1.08 Lecture Room";

        // this checks that the parser rejects the class because the start time is after the end time.
        assertThrows(CsvFormatException.class, () -> parser.parseLines(List.of(HEADER, row)));
    }
}
