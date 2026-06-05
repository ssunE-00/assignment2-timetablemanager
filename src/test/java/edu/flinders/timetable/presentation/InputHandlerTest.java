package edu.flinders.timetable.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.ByteArrayInputStream;
import java.util.Scanner;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class InputHandlerTest {

    private InputHandler handler(String input) {
        return new InputHandler(new Scanner(new ByteArrayInputStream(input.getBytes())));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("IH1 - readLine trims leading and trailing whitespace")
    void ih1ReadLineTrim() {
        InputHandler h = handler("  hello  \n");
        assertEquals("hello", h.readLine("Prompt: "));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("IH2 - readInt returns the parsed integer from valid input")
    void ih2ReadIntValid() {
        InputHandler h = handler("42\n");
        assertEquals(42, h.readInt("Prompt: ", -1));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("IH3 - readInt returns default value when input is blank")
    void ih3ReadIntBlankReturnsDefault() {
        InputHandler h = handler("\n");
        assertEquals(-1, h.readInt("Prompt: ", -1));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("IH4 - readInt returns default value when input is not a number")
    void ih4ReadIntInvalidReturnsDefault() {
        InputHandler h = handler("abc\n");
        assertEquals(-1, h.readInt("Prompt: ", -1));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("IH5 - confirm returns true for y")
    void ih5ConfirmAcceptsY() {
        InputHandler h = handler("y\n");
        assertTrue(h.confirm("Confirm?"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("IH6 - confirm returns true for yes")
    void ih6ConfirmAcceptsYes() {
        InputHandler h = handler("yes\n");
        assertTrue(h.confirm("Confirm?"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("IH7 - confirm returns false for no")
    void ih7ConfirmRejectsNo() {
        InputHandler h = handler("no\n");
        assertFalse(h.confirm("Confirm?"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("IH8 - confirm returns false for blank input")
    void ih8ConfirmRejectsBlank() {
        InputHandler h = handler("\n");
        assertFalse(h.confirm("Confirm?"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Core")
    @DisplayName("IH9 - readCsvValues trims and splits comma separated input")
    void ih9ReadCsvValuesTrimsAndSplits() {
        InputHandler h = handler("A, B , , C\n");
        Set<String> values = h.readCsvValues("Enter: ");
        assertEquals(3, values.size());
        assertTrue(values.contains("A"));
        assertTrue(values.contains("B"));
        assertTrue(values.contains("C"));
    }

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("IH10 - readCsvValues returns empty set for blank input")
    void ih10ReadCsvValuesBlankReturnsEmpty() {
        InputHandler h = handler("\n");
        Set<String> values = h.readCsvValues("Enter: ");
        assertTrue(values.isEmpty());
    }
}
