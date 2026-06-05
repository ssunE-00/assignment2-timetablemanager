package edu.flinders.timetable.model;

import edu.flinders.timetable.util.TextUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class SearchCriteria {
    private String topicCode;
    private String topicName;
    private String attendanceMode;
    private String campus;
    private Integer semester;
    private Integer availabilityNumber;
    private String classFormat;
    private Integer classInstance;
    private LocalDate firstClassDate;
    private LocalDate lastClassDate;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String building;
    private String room;

    public boolean matches(ClassRecord record) {
        return TextUtil.containsIgnoreCase(record.getTopicCode(), topicCode)
                && TextUtil.containsIgnoreCase(record.getTopicName(), topicName)
                && TextUtil.containsIgnoreCase(record.getAttendanceMode(), attendanceMode)
                && TextUtil.containsIgnoreCase(record.getCampus(), campus)
                && matchesInteger(record.getSemester(), semester)
                && matchesInteger(record.getAvailabilityNumber(), availabilityNumber)
                && TextUtil.containsIgnoreCase(record.getClassFormat(), classFormat)
                && matchesInteger(record.getClassInstance(), classInstance)
                && matchesObject(record.getFirstClassDate(), firstClassDate)
                && matchesObject(record.getLastClassDate(), lastClassDate)
                && matchesObject(record.getDay(), day)
                && matchesObject(record.getStartTime(), startTime)
                && matchesObject(record.getEndTime(), endTime)
                && TextUtil.containsIgnoreCase(record.getBuilding(), building)
                && TextUtil.containsIgnoreCase(record.getRoom(), room);
    }

    private boolean matchesInteger(int actual, Integer expected) {
        return expected == null || actual == expected;
    }

    private boolean matchesObject(Object actual, Object expected) {
        return expected == null || expected.equals(actual);
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

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getAvailabilityNumber() {
        return availabilityNumber;
    }

    public void setAvailabilityNumber(Integer availabilityNumber) {
        this.availabilityNumber = availabilityNumber;
    }

    public String getClassFormat() {
        return classFormat;
    }

    public void setClassFormat(String classFormat) {
        this.classFormat = classFormat;
    }

    public Integer getClassInstance() {
        return classInstance;
    }

    public void setClassInstance(Integer classInstance) {
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
}
