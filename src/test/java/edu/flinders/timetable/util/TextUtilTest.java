package edu.flinders.timetable.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class TextUtilTest {

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU11.01 - isBlank returns true for null")
    void tu1101IsBlankTrueForNull() {
        assertTrue(TextUtil.isBlank(null));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU11.02 - isBlank returns true for empty string")
    void tu1102IsBlankTrueForEmpty() {
        assertTrue(TextUtil.isBlank(""));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU11.03 - isBlank returns true for whitespace only string")
    void tu1103IsBlankTrueForWhitespace() {
        assertTrue(TextUtil.isBlank("   "));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU11.04 - isBlank returns false for non blank string")
    void tu1104IsBlankFalseForNonBlank() {
        assertFalse(TextUtil.isBlank("hello"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU11.05 - clean returns empty string for null")
    void tu1105CleanNullReturnsEmpty() {
        assertEquals("", TextUtil.clean(null));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU11.06 - clean trims leading and trailing whitespace")
    void tu1106CleanTrimsWhitespace() {
        assertEquals("hello", TextUtil.clean("  hello  "));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU11.07 - containsIgnoreCase returns true for blank search term")
    void tu1107ContainsIgnoreCaseTrueForBlankSearch() {
        assertTrue(TextUtil.containsIgnoreCase("anything", null));
        assertTrue(TextUtil.containsIgnoreCase("anything", ""));
        assertTrue(TextUtil.containsIgnoreCase("anything", "   "));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU11.08 - containsIgnoreCase returns false for null source")
    void tu1108ContainsIgnoreCaseFalseForNullSource() {
        assertFalse(TextUtil.containsIgnoreCase(null, "hello"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU11.09 - containsIgnoreCase is case insensitive")
    void tu1109ContainsIgnoreCaseCaseInsensitive() {
        assertTrue(TextUtil.containsIgnoreCase("Hello World", "hello"));
        assertTrue(TextUtil.containsIgnoreCase("hello world", "HELLO"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU11.10 - containsIgnoreCase returns false when substring not found")
    void tu1110ContainsIgnoreCaseFalseWhenNotFound() {
        assertFalse(TextUtil.containsIgnoreCase("Hello", "xyz"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU11.11 - equalsIgnoreCase returns true for equal strings differing by case")
    void tu1111EqualsIgnoreCaseTrueForEqualStrings() {
        assertTrue(TextUtil.equalsIgnoreCase("Hello", "hello"));
        assertTrue(TextUtil.equalsIgnoreCase("  Hello  ", "hello"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("TU11.12 - equalsIgnoreCase returns false when one is null and other is not")
    void tu1112EqualsIgnoreCaseFalseWhenOneNull() {
        assertFalse(TextUtil.equalsIgnoreCase(null, "hello"));
        assertFalse(TextUtil.equalsIgnoreCase("hello", null));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("TU11.13 - equalsIgnoreCase returns true when both are null")
    void tu1113EqualsIgnoreCaseTrueWhenBothNull() {
        assertTrue(TextUtil.equalsIgnoreCase(null, null));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("TU11.14 - firstNonBlank returns new value when it is not blank")
    void tu1114FirstNonBlankReturnsNewValue() {
        assertEquals("new", TextUtil.firstNonBlank("new", "old"));
        assertEquals("new", TextUtil.firstNonBlank("  new  ", "old"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("TU11.15 - firstNonBlank returns old value when new value is blank")
    void tu1115FirstNonBlankReturnsOldWhenNewBlank() {
        assertEquals("old", TextUtil.firstNonBlank(null, "old"));
        assertEquals("old", TextUtil.firstNonBlank("", "old"));
        assertEquals("old", TextUtil.firstNonBlank("   ", "old"));
    }
}
