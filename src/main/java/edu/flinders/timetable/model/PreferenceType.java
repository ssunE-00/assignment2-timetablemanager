package edu.flinders.timetable.model;

public enum PreferenceType {
    BEDFORD_PARK("Bedford Park"),
    TONSLEY("Tonsley"),
    FLINDERS_CITY("Flinders City Campus"),
    SAME_CAMPUS("All at the same campus"),
    MORNINGS("Mornings"),
    AFTERNOONS("Afternoons"),
    MONDAY("Mondays"),
    TUESDAY("Tuesdays"),
    WEDNESDAY("Wednesdays"),
    THURSDAY("Thursdays"),
    FRIDAY("Fridays"),
    EVENLY_SPREAD("Evenly spread classes across days"),
    COMPACT_DAYS("Compact classes to as few days as possible");

    private final String displayName;

    PreferenceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
