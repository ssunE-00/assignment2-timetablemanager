# Timetable Optimizer

Minimal Java 17 console application for ENGR3791 Assignment 2: Prompt Engineering and Unit Testing.

## What this project includes

- Java 17 Maven project
- Console-only interface
- CSV import for class data
- Browse, view, search, edit, and delete class records
- Basic timetable generation
- Timetable browse, view, edit/swap, delete, and export
- Simple layered structure matching the proposed architecture:
  - `presentation`
  - `application`
  - `service`
  - `model`
  - `data`
  - `result`
  - `util`
- Empty JUnit scaffold files only

## Academic integrity note

The `src/test/java` files are intentionally blank scaffolds. They include package declarations, imports, lifecycle placeholders, and TODO comments only. They do not include generated JUnit test cases, assertions, expected outputs, or test data. The group should write the actual JUnit tests manually.

## Run the app

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="edu.flinders.timetable.Main"
```

If the Maven exec plugin is not configured in your IDE, you can run `Main.java` directly from IntelliJ.

## Sample CSV

A small example file is included at:

```text
examples/sample-topic-data.csv
```

## Exported timetables

The app can export timetables to CSV. The default suggested folder is `exports/`, which is ignored by Git.
