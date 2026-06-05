package edu.flinders.timetable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    @Test
    @Tag("kang0201")
    @Tag("Additional")
    @DisplayName("M0.01 - Main exits cleanly when user selects option 0")
    void mainDoesNotHang() {
        InputStream originalIn = System.in;
        try {
            // simulate user entering "0" to choose the Exit option from the main menu.
            System.setIn(new ByteArrayInputStream("0\n".getBytes()));
            assertDoesNotThrow(() -> Main.main(new String[]{}));
        } finally {
            System.setIn(originalIn);
        }
    }
}
