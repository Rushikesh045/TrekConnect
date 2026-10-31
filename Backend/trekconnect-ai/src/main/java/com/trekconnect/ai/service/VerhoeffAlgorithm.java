package com.trekconnect.ai.service;

/**
 * Verhoeff Dihedral Group (D5) Checksum Algorithm.
 * 
 * Standard error-detection algorithm used by UIDAI (Government of India)
 * for 12-digit Aadhaar number check-digit generation and mathematical verification.
 * Catches 100% of single-digit errors and 100% of adjacent transposition errors.
 */
public class VerhoeffAlgorithm {

    // The multiplication table (dihedral group D5)
    private static final int[][] D_TABLE = new int[][] {
        {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
        {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
        {2, 3, 4, 0, 1, 7, 8, 9, 5, 6},
        {3, 4, 0, 1, 2, 8, 9, 5, 6, 7},
        {4, 0, 1, 2, 3, 9, 5, 6, 7, 8},
        {5, 9, 8, 7, 6, 0, 4, 3, 2, 1},
        {6, 5, 9, 8, 7, 1, 0, 4, 3, 2},
        {7, 6, 5, 9, 8, 2, 1, 0, 4, 3},
        {8, 7, 6, 5, 9, 3, 2, 1, 0, 4},
        {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}
    };

    // The permutation table
    private static final int[][] P_TABLE = new int[][] {
        {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
        {1, 5, 7, 6, 2, 8, 3, 0, 9, 4},
        {5, 8, 0, 3, 7, 9, 6, 1, 4, 2},
        {8, 9, 1, 6, 0, 4, 3, 5, 2, 7},
        {9, 4, 5, 3, 1, 2, 6, 8, 7, 0},
        {4, 2, 8, 6, 5, 7, 3, 9, 0, 1},
        {2, 7, 9, 3, 8, 0, 6, 4, 1, 5},
        {7, 0, 4, 6, 9, 1, 3, 2, 5, 8}
    };

    // The inverse table
    private static final int[] INV_TABLE = {0, 4, 3, 2, 1, 5, 6, 7, 8, 9};

    /**
     * Validates whether the given numeric string satisfies the Verhoeff checksum.
     * @param num Full Aadhaar number including check digit
     * @return true if checksum passes, false otherwise
     */
    public static boolean validateVerhoeff(String num) {
        if (num == null) return false;
        String clean = num.replaceAll("\\s+", "");
        if (clean.length() != 12) return false;

        int c = 0;
        int[] digits = new int[clean.length()];
        for (int i = 0; i < clean.length(); i++) {
            char ch = clean.charAt(i);
            if (!Character.isDigit(ch)) {
                return false;
            }
            digits[i] = ch - '0';
        }

        // Evaluate from right to left
        for (int i = 0; i < digits.length; i++) {
            c = D_TABLE[c][P_TABLE[(i % 8)][digits[digits.length - i - 1]]];
        }

        return c == 0;
    }

    /**
     * Generates the Verhoeff check digit for an 11-digit or n-digit number prefix.
     */
    public static String generateVerhoeff(String num) {
        if (num == null) return "";
        String clean = num.replaceAll("\\s+", "");
        int c = 0;
        int[] digits = new int[clean.length()];
        for (int i = 0; i < clean.length(); i++) {
            digits[i] = clean.charAt(i) - '0';
        }

        for (int i = 0; i < digits.length; i++) {
            c = D_TABLE[c][P_TABLE[((i + 1) % 8)][digits[digits.length - i - 1]]];
        }

        return Integer.toString(INV_TABLE[c]);
    }
}
