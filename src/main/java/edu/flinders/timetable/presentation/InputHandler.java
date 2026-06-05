package edu.flinders.timetable.presentation;

import edu.flinders.timetable.util.TextUtil;

import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class InputHandler {
    private final Scanner scanner;

    public InputHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt, int defaultValue) {
        String input = readLine(prompt);
        if (TextUtil.isBlank(input)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(Ansi.RED + "Invalid number. Using default: " + defaultValue + Ansi.RESET);
            return defaultValue;
        }
    }

    public boolean confirm(String prompt) {
        String input = readLine(prompt + " (y/n): ");
        return input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes");
    }

    public Set<String> readCsvValues(String prompt) {
        Set<String> values = new LinkedHashSet<>();
        String input = readLine(prompt);
        if (TextUtil.isBlank(input)) {
            return values;
        }
        for (String value : input.split(",")) {
            if (!TextUtil.isBlank(value)) {
                values.add(value.trim());
            }
        }
        return values;
    }
}
