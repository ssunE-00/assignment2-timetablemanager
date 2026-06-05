package edu.flinders.timetable.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class ClassRecord {
    private String topicCode;
    private String topicName;
    private String attendanceMode;
    private String campus;
    private int semester;
    private int availabilityNumber;
    private String classFormat;
    private int classInstance;
    private LocalDate firstClassDate;
    private LocalDate lastClassDate;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String building;
    private String room;

    public ClassRecord(String topicCode, String topicName, String attendanceMode, String campus,
                       int semester, int availabilityNumber, String classFormat, int classInstance,
                       LocalDate firstClassDate, LocalDate lastClassDate, DayOfWeek day,
                       LocalTime startTime, LocalTime endTime, String building, String room) {
        this.topicCode = topicCode;
        this.topicName = topicName;
        this.attendanceMode = attendanceMode;
        this.campus = campus;
        this.semester = semester;
        this.availabilityNumber = availabilityNumber;
        this.classFormat = classFormat;
        this.classInstance = classInstance;
        this.firstClassDate = firstClassDate;
        this.lastClassDate = lastClassDate;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.building = building;
        this.room = room;
    }

    public String getTopicCode() {
        return topicCode;
    }

    public void setTopicCode(String topicCode) {
        this.topicCode = topicCode;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getAttendanceMode() {
        return attendanceMode;
    }

    public void setAttendanceMode(String attendanceMode) {
        this.attendanceMode = attendanceMode;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getAvailabilityNumber() {
        return availabilityNumber;
    }

    public void setAvailabilityNumber(int availabilityNumber) {
        this.availabilityNumber = availabilityNumber;
    }

    public String getClassFormat() {
        return classFormat;
    }

    public void setClassFormat(String classFormat) {
        this.classFormat = classFormat;
    }

    public int getClassInstance() {
        return classInstance;
    }

    public void setClassInstance(int classInstance) {
        this.classInstance = classInstance;
    }

    public LocalDate getFirstClassDate() {
        return firstClassDate;
    }

    public void setFirstClassDate(LocalDate firstClassDate) {
        this.firstClassDate = firstClassDate;
    }

    public LocalDate getLastClassDate() {
        return lastClassDate;
    }

    public void setLastClassDate(LocalDate lastClassDate) {
        this.lastClassDate = lastClassDate;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String importKey() {
        return String.join("|",
                nullSafe(topicCode), nullSafe(topicName), nullSafe(attendanceMode), nullSafe(campus),
                String.valueOf(semester), String.valueOf(availabilityNumber), nullSafe(classFormat),
                String.valueOf(classInstance), String.valueOf(firstClassDate), String.valueOf(lastClassDate),
                String.valueOf(day));
    }

    public String groupKey() {
        return String.join("|",
                nullSafe(topicCode), nullSafe(topicName), nullSafe(attendanceMode), nullSafe(campus),
                String.valueOf(semester), String.valueOf(availabilityNumber), nullSafe(classFormat),
                String.valueOf(classInstance));
    }

    public String choiceKey() {
        return String.join("|", nullSafe(topicCode), nullSafe(classFormat));
    }

    public boolean isLecture() {
        return classFormat != null && classFormat.toLowerCase().contains("lecture");
    }

    public ClassRecord copy() {
        return new ClassRecord(topicCode, topicName, attendanceMode, campus, semester, availabilityNumber,
                classFormat, classInstance, firstClassDate, lastClassDate, day, startTime, endTime, building, room);
    }

    public String displayLine() {
        return topicCode + " " + topicName + " | " + campus + " | S" + semester + " | "
                + classFormat + " " + classInstance + " | " + day + " " + startTime + "-" + endTime
                + " | " + building + ", " + room;
    }

    private String nullSafe(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    public String toString() {
        return displayLine();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassRecord that)) {
            return false;
        }
        return Objects.equals(importKey(), that.importKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(importKey());
    }
}
