package com.antarctic;

public class InputValidator {
    public static boolean isValidMeasurement(double value, double maxLimit) {
        if (value <= 0) {
            System.out.println("Value cannot be 0 or negative");
            return false;
        }
        if (value > maxLimit) {
            System.out.println("Value cannot exceed " + maxLimit);
            return false;
        }
        return true;
    }

    public static String formatIslandName(String input) {
        if (input == null || input.isEmpty()) {
            return "Unknown";
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
