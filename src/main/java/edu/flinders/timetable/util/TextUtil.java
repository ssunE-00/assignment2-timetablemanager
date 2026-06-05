package edu.flinders.timetable.util;

import java.util.Locale;

public final class TextUtil {
    private TextUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    public static boolean containsIgnoreCase(String source, String search) {
        if (isBlank(search)) {
            return true;
        }
        if (source == null) {
            return false;
        }
        return source.toLowerCase(Locale.ROOT).contains(search.trim().toLowerCase(Locale.ROOT));
    }

    public static boolean equalsIgnoreCase(String first, String second) {
        if (first == null || second == null) {
            return first == null && second == null;
        }
        return first.trim().equalsIgnoreCase(second.trim());
    }

    public static String firstNonBlank(String newValue, String oldValue) {
        return isBlank(newValue) ? oldValue : newValue.trim();
    }
}
