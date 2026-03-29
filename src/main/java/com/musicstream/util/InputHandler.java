package com.musicstream.util;

import java.util.Scanner;

/**
 * Centralised input handler.
 * A single Scanner instance is shared across the whole application.
 * Every read goes through here so the EXIT keyword can be caught globally.
 */
public class InputHandler {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String EXIT_KEYWORD = "EXIT";

    private InputHandler() {}

    /**
     * Reads a line of input. If the user types "EXIT" (case-insensitive)
     * the program terminates gracefully immediately.
     */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase(EXIT_KEYWORD)) {
            System.out.println("\nGoodbye! Thanks for using StreamFlow.");
            System.exit(0);
        }
        return input;
    }

    /**
     * Reads an integer from the user, re-prompting on invalid input.
     */
    public static int readInt(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("⚠ Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads a double from the user, re-prompting on invalid input.
     */
    public static double readDouble(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                System.out.println("⚠ Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a yes/no confirmation from the user.
     */
    public static boolean readBoolean(String prompt) {
        while (true) {
            String raw = readLine(prompt + " (yes/no): ").toLowerCase();
            if (raw.equals("yes") || raw.equals("y")) return true;
            if (raw.equals("no") || raw.equals("n")) return false;
            System.out.println("⚠ Please enter 'yes' or 'no'.");
        }
    }
}
