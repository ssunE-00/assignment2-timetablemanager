package edu.flinders.timetable.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InputHandlerTest {

    private InputHandler handler(String input) {
        return new InputHandler(new Scanner(new ByteArrayInputStream(input.getBytes())));
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IH1 - readLine trims input")
    void ih1_read_line_trim() {
        InputHandler h = handler("  hello  \n");
        assertEquals("hello", h.readLine("Prompt: "));
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IH2 - readInt returns number")
    void ih2_read_int_valid() {
        InputHandler h = handler("42\n");
        assertEquals(42, h.readInt("Prompt: ", -1));
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IH3 - readInt returns default on blank")
    void ih3_read_int_blank() {
        InputHandler h = handler("\n");
        assertEquals(-1, h.readInt("Prompt: ", -1));
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IH4 - readInt handles invalid number")
    void ih4_read_int_invalid() {
        InputHandler h = handler("abc\n");
        assertEquals(-1, h.readInt("Prompt: ", -1));
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IH5 - confirm accepts yes")
    void ih5_confirm_yes() {
        InputHandler h = handler("y\n");
        assertTrue(h.confirm("Confirm?"));
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IH6 - confirm accepts yes full word")
    void ih6_confirm_yes_full() {
        InputHandler h = handler("yes\n");
        assertTrue(h.confirm("Confirm?"));
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IH7 - confirm rejects no")
    void ih7_confirm_no() {
        InputHandler h = handler("no\n");
        assertFalse(h.confirm("Confirm?"));
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IH8 - confirm rejects blank")
    void ih8_confirm_blank() {
        InputHandler h = handler("\n");
        assertFalse(h.confirm("Confirm?"));
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IH9 - CSV parsing trims and splits")
    void ih9_csv_values() {
        InputHandler h = handler("A, B , , C\n");
        Set<String> values = h.readCsvValues("Enter: ");

        assertEquals(3, values.size());
        assertTrue(values.contains("A"));
        assertTrue(values.contains("B"));
        assertTrue(values.contains("C"));
    }

    @Test
    @Tag("hone0038")
    @DisplayName("IH10 - CSV blank returns empty set")
    void ih10_csv_blank() {
        InputHandler h = handler("\n");
        Set<String> values = h.readCsvValues("Enter: ");

        assertTrue(values.isEmpty());
    }
}