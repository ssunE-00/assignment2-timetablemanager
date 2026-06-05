# GitHub collaboration guide

This guide is written for a small assignment group using GitHub to share the Java codebase, manually write JUnit tests, and prepare the final submission.

## 1. Create the repository

1. Go to GitHub and create a new repository.
2. Recommended repository name: `engr3791-timetable-optimizer`.
3. Set the repository to **Private**.
4. Do not initialise with a README if you are uploading this project folder, because this project already contains one.
5. After creating the repository, copy the GitHub repository URL.

## 2. Upload the project for the first time

From the root folder of this project, run:

```bash
git init
git add .
git commit -m "Initial timetable optimizer codebase with testing scaffold"
git branch -M main
git remote add origin <PASTE-YOUR-GITHUB-REPO-URL-HERE>
git push -u origin main
```

## 3. Invite group members

1. Open the repository on GitHub.
2. Go to **Settings**.
3. Go to **Collaborators**.
4. Add each group member using their GitHub username or email.
5. Ask each person to accept the invitation.

## 4. Recommended branch workflow

Avoid everyone pushing directly to `main`. Each person should create their own branch.

Example branch names:

```text
feature/csv-import-fixes
test/csv-parser-tests
test/schedule-service-tests
docs/section-2-prompts
docs/gantt-chart-updates
```

To create a branch:

```bash
git checkout main
git pull
git checkout -b test/csv-parser-tests
```

After making changes:

```bash
git status
git add .
git commit -m "Add manually written CSV parser tests"
git push -u origin test/csv-parser-tests
```

Then open a Pull Request on GitHub.

## 5. Pull request rules

Use Pull Requests so the group can review changes before they enter `main`.

Suggested rules:

- At least one other person checks the Pull Request.
- Do not merge code that does not compile.
- Do not merge generated JUnit test logic.
- Keep each Pull Request focused on one area.
- Add a short note explaining what changed and why.

## 6. Manual testing responsibilities

The scaffold files under `src/test/java` are blank on purpose. Each group member should manually write their assigned tests.

When writing tests, make sure your test method names and annotations align with the Gantt chart:

```java
@DisplayName("1.01 <your test case name>")
@Tag("Critical")
@Tag("<assigned person>")
```

Use the test IDs, priority labels, and assigned names consistently across:

- JUnit `@DisplayName`
- JUnit `@Tag`
- Gantt chart
- Test case specification spreadsheet
- Test outcome summary

## 7. Keep the project clean

Before pushing, run:

```bash
mvn clean test
```

At minimum, confirm the main code compiles:

```bash
mvn clean compile
```

Do not commit:

- `target/`
- `.idea/`
- `.iml` files
- exported timetable CSV files
- temporary notes that are not part of the submission

These are already listed in `.gitignore`.

## 8. Suggested task split

A simple split for five group members could be:

- Person 1: CSV import, parser, repository tests
- Person 2: class browse/search/edit/delete tests
- Person 3: timetable generation and settings tests
- Person 4: schedule clash and commute gap tests
- Person 5: export, documentation, Gantt chart, and presentation coordination

Adjust this based on who is comfortable with each area.

## 9. Final submission preparation

Before submission:

1. Pull the latest `main` branch.
2. Run `mvn clean test`.
3. Confirm the project opens in IntelliJ.
4. Confirm the app can run from `Main.java`.
5. Confirm coverage can be shown in IntelliJ.
6. Zip the full IntelliJ/Maven project.
7. Submit the zipped project, report document, Gantt chart spreadsheet, and test case spreadsheet as required.
