package edu.flinders.timetable.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TimetableSettings {
    private String timetableName;
    private Set<Integer> semesters = new LinkedHashSet<>();
    private Set<String> topicCodes = new LinkedHashSet<>();
    private Set<String> campuses = new LinkedHashSet<>();
    private boolean allowLectureOverlap;
    private List<PreferenceType> preferences = new ArrayList<>();

    public TimetableSettings() {
    }

    public TimetableSettings copy() {
        TimetableSettings copy = new TimetableSettings();
        copy.timetableName = timetableName;
        copy.semesters = new LinkedHashSet<>(semesters);
        copy.topicCodes = new LinkedHashSet<>(topicCodes);
        copy.campuses = new LinkedHashSet<>(campuses);
        copy.allowLectureOverlap = allowLectureOverlap;
        copy.preferences = new ArrayList<>(preferences);
        return copy;
    }

    public String getTimetableName() {
        return timetableName;
    }

    public void setTimetableName(String timetableName) {
        this.timetableName = timetableName;
    }

    public Set<Integer> getSemesters() {
        return semesters;
    }

    public void setSemesters(Set<Integer> semesters) {
        this.semesters = semesters;
    }

    public Set<String> getTopicCodes() {
        return topicCodes;
    }

    public void setTopicCodes(Set<String> topicCodes) {
        this.topicCodes = topicCodes;
    }

    public Set<String> getCampuses() {
        return campuses;
    }

    public void setCampuses(Set<String> campuses) {
        this.campuses = campuses;
    }

    public boolean isAllowLectureOverlap() {
        return allowLectureOverlap;
    }

    public void setAllowLectureOverlap(boolean allowLectureOverlap) {
        this.allowLectureOverlap = allowLectureOverlap;
    }

    public List<PreferenceType> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<PreferenceType> preferences) {
        this.preferences = preferences;
    }
}
