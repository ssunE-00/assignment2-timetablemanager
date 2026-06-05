package edu.flinders.timetable.data;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.util.TextUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CsvParser {
    private static final int EXPECTED_COLUMNS = 8;
    private static final int DEFAULT_YEAR = 2026;
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("H:mm");

    public List<ClassRecord> parse(Path filePath) {
        try {
            List<String> lines = Files.readAllLines(filePath);
            return parseLines(lines);
        } catch (IOException e) {
            throw new CsvFormatException("Could not read CSV file: " + filePath, e);
        }
    }

    public List<ClassRecord> parseLines(List<String> lines) {
        if (lines == null || lines.size() < 2) {
            throw new CsvFormatException("CSV file must include a header row and at least one data row.");
        }

        validateHeader(splitCsvLine(lines.get(0)));
        List<ClassRecord> records = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (TextUtil.isBlank(line)) {
                continue;
            }
            List<String> values = normaliseColumns(splitCsvLine(line));
            if (values.size() != EXPECTED_COLUMNS) {
                throw new CsvFormatException("Row " + (i + 1) + " has " + values.size()
                        + " columns. Expected " + EXPECTED_COLUMNS + ".");
            }
            records.add(parseRecord(values, i + 1));
        }
        return records;
    }

    private void validateHeader(List<String> header) {
        List<String> expected = List.of("Topic", "Availability", "Class", "Class instance", "Date", "Day", "Time", "Location");
        if (header.size() != expected.size()) {
            throw new CsvFormatException("CSV header has the wrong number of columns.");
        }
        for (int i = 0; i < expected.size(); i++) {
            if (!expected.get(i).equalsIgnoreCase(header.get(i).trim())) {
                throw new CsvFormatException("CSV header column " + (i + 1) + " should be '" + expected.get(i) + "'.");
            }
        }
    }

    private ClassRecord parseRecord(List<String> values, int rowNumber) {
        try {
            String[] topic = parseTopic(values.get(0));
            AvailabilityParts availability = parseAvailability(values.get(1));
            String classFormat = TextUtil.clean(values.get(2));
            int classInstance = parsePositiveInt(values.get(3), "class instance");
            LocalDate[] dateRange = parseDateRange(values.get(4));
            DayOfWeek day = parseDay(values.get(5));
            LocalTime[] timeRange = parseTimeRange(values.get(6));
            String[] location = parseLocation(values.get(7));

            return new ClassRecord(topic[0], topic[1], availability.attendanceMode(), availability.campus(),
                    availability.semester(), availability.availabilityNumber(), classFormat, classInstance,
                    dateRange[0], dateRange[1], day, timeRange[0], timeRange[1], location[0], location[1]);
        } catch (RuntimeException e) {
            throw new CsvFormatException("Row " + rowNumber + " could not be parsed: " + e.getMessage(), e);
        }
    }

    private String[] parseTopic(String value) {
        String cleaned = TextUtil.clean(value);
        int firstSpace = cleaned.indexOf(' ');
        if (firstSpace <= 0 || firstSpace == cleaned.length() - 1) {
            throw new CsvFormatException("Topic must include a topic code and topic name.");
        }
        return new String[]{cleaned.substring(0, firstSpace).trim(), cleaned.substring(firstSpace + 1).trim()};
    }

    private AvailabilityParts parseAvailability(String value) {
        String[] parts = value.split(" - ");
        if (parts.length < 4) {
            throw new CsvFormatException("Availability must include attendance mode, campus, semester, and availability number.");
        }
        String attendanceMode = parts[0].trim();
        String campus = parts[1].trim();
        int semester = parseSemester(parts[parts.length - 2]);
        int availabilityNumber = parsePositiveInt(parts[parts.length - 1], "availability number");
        return new AvailabilityParts(attendanceMode, campus, semester, availabilityNumber);
    }

    private int parseSemester(String value) {
        String cleaned = TextUtil.clean(value).toUpperCase(Locale.ROOT).replace("SEMESTER", "S").replace(" ", "");
        if (cleaned.equals("S1") || cleaned.equals("1")) {
            return 1;
        }
        if (cleaned.equals("S2") || cleaned.equals("2")) {
            return 2;
        }
        throw new CsvFormatException("Semester must be S1 or S2.");
    }

    private int parsePositiveInt(String value, String fieldName) {
        int parsed = Integer.parseInt(TextUtil.clean(value));
        if (parsed <= 0) {
            throw new CsvFormatException(fieldName + " must be greater than zero.");
        }
        return parsed;
    }

    private LocalDate[] parseDateRange(String value) {
        String[] parts = value.split(" - ");
        if (parts.length != 2) {
            throw new CsvFormatException("Date must be formatted as first date - last date.");
        }
        return new LocalDate[]{parseDate(parts[0]), parseDate(parts[1])};
    }

    private LocalDate parseDate(String value) {
        String cleaned = TextUtil.clean(value);
        try {
            if (cleaned.matches(".*\\d{4}$")) {
                return LocalDate.parse(cleaned, SHORT_DATE);
            }
            return LocalDate.parse(cleaned + " " + DEFAULT_YEAR, SHORT_DATE);
        } catch (RuntimeException e) {
            throw new CsvFormatException("Invalid date: " + value);
        }
    }

    private DayOfWeek parseDay(String value) {
        return DayOfWeek.valueOf(TextUtil.clean(value).toUpperCase(Locale.ROOT));
    }

    private LocalTime[] parseTimeRange(String value) {
        String[] parts = value.split(" - ");
        if (parts.length != 2) {
            throw new CsvFormatException("Time must be formatted as start time - end time.");
        }
        LocalTime start = LocalTime.parse(parts[0].trim(), TIME);
        LocalTime end = LocalTime.parse(parts[1].trim(), TIME);
        if (!start.isBefore(end)) {
            throw new CsvFormatException("Start time must be before end time.");
        }
        return new LocalTime[]{start, end};
    }

    private String[] parseLocation(String value) {
        String cleaned = TextUtil.clean(value);
        int comma = cleaned.indexOf(',');
        if (comma < 0) {
            return new String[]{cleaned, ""};
        }
        return new String[]{cleaned.substring(0, comma).trim(), cleaned.substring(comma + 1).trim()};
    }

    private List<String> normaliseColumns(List<String> values) {
        if (values.size() <= EXPECTED_COLUMNS) {
            return values;
        }

        // The assignment data stores location as "Building, room". Some CSV exports quote this field,
        // while simple hand-written examples may leave it unquoted. If extra columns appear, keep the
        // first seven fields as-is and join the remaining fields back into the Location value.
        List<String> normalised = new ArrayList<>(values.subList(0, EXPECTED_COLUMNS - 1));
        normalised.add(String.join(", ", values.subList(EXPECTED_COLUMNS - 1, values.size())));
        return normalised;
    }

    private List<String> splitCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        values.add(current.toString().trim());
        return values;
    }

    private record AvailabilityParts(String attendanceMode, String campus, int semester, int availabilityNumber) {
    }
}
