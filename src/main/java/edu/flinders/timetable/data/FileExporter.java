package edu.flinders.timetable.data;

import edu.flinders.timetable.model.ClassRecord;
import edu.flinders.timetable.model.Timetable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileExporter {
    public Path exportTimetableToCsv(Timetable timetable, Path outputPath) {
        try {
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }
            Files.write(outputPath, toCsvLines(timetable));
            return outputPath;
        } catch (IOException e) {
            throw new IllegalStateException("Could not export timetable to " + outputPath, e);
        }
    }

    private List<String> toCsvLines(Timetable timetable) {
        List<String> lines = new ArrayList<>();
        lines.add("Topic Code,Topic Name,Attendance Mode,Campus,Semester,Availability Number,Class,Class Instance,First Class Date,Last Class Date,Day,Start Time,End Time,Building,Room");
        for (ClassRecord record : timetable.getRecords()) {
            lines.add(String.join(",",
                    escape(record.getTopicCode()),
                    escape(record.getTopicName()),
                    escape(record.getAttendanceMode()),
                    escape(record.getCampus()),
                    String.valueOf(record.getSemester()),
                    String.valueOf(record.getAvailabilityNumber()),
                    escape(record.getClassFormat()),
                    String.valueOf(record.getClassInstance()),
                    String.valueOf(record.getFirstClassDate()),
                    String.valueOf(record.getLastClassDate()),
                    String.valueOf(record.getDay()),
                    String.valueOf(record.getStartTime()),
                    String.valueOf(record.getEndTime()),
                    escape(record.getBuilding()),
                    escape(record.getRoom())));
        }
        return lines;
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.replace("\"", "\"\"");
        if (cleaned.contains(",") || cleaned.contains("\"")) {
            return "\"" + cleaned + "\"";
        }
        return cleaned;
    }
}
