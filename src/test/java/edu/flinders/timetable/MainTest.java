package edu.flinders.timetable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    @Test
    @Tag("hone0038")
    void mainDoesNotHang() {

        // Simulate user typing "exit" immediately
        System.setIn(new ByteArrayInputStream("exit\n".getBytes()));

        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}