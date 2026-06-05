package edu.flinders.timetable.presentation;

public class MenuSystem {
    public void printTitle() {
        System.out.println(Ansi.CYAN + Ansi.BOLD);
        System.out.println("  _______ _                _        _     _      ");
        System.out.println(" |__   __(_)              | |      | |   | |     ");
        System.out.println("    | |   _ _ __ ___   ___| |_ __ _| |__ | | ___ ");
        System.out.println("    | |  | | '_ ` _ \\ / _ \\ __/ _` | '_ \\| |/ _ \\");
        System.out.println("    | |  | | | | | | |  __/ || (_| | |_) | |  __/");
        System.out.println("    |_|  |_|_| |_| |_|\\___|\\__\\__,_|_.__/|_|\\___|");
        System.out.println("       Optimizer");
        System.out.println(Ansi.RESET);
    }

    public void printMainMenu() {
        System.out.println(Ansi.BOLD + "\nMain Menu" + Ansi.RESET);
        System.out.println("1. Import classes from CSV");
        System.out.println("2. Browse classes");
        System.out.println("3. View all class records");
        System.out.println("4. Search classes");
        System.out.println("5. Edit a class record");
        System.out.println("6. Delete a class record");
        System.out.println("7. Generate timetable");
        System.out.println("8. Browse timetables");
        System.out.println("9. View timetable");
        System.out.println("10. Edit timetable by swapping class instance");
        System.out.println("11. Delete timetable");
        System.out.println("12. Export timetable");
        System.out.println("0. Exit");
    }
}
