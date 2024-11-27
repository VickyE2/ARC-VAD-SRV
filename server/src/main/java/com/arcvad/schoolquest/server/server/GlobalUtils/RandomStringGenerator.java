package com.arcvad.schoolquest.server.server.GlobalUtils;

import java.security.SecureRandom;

public class RandomStringGenerator {

    // Character pools
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^*()-_=+?/";

    private final SecureRandom random = new SecureRandom();

    // Singleton instance
    private static final RandomStringGenerator INSTANCE = new RandomStringGenerator();

    // Private constructor to prevent instantiation
    private RandomStringGenerator() {}

    // Public method to access the singleton instance
    public static RandomStringGenerator getInstance() {
        return INSTANCE;
    }

    /**
     * Generates a random string with the specified options.
     *
     * @param length       Length of the string to generate.
     * @param includeUpper Include uppercase letters.
     * @param includeLower Include lowercase letters.
     * @param includeDigits Include digits.
     * @param includeSymbols Include special symbols.
     * @return A random string.
     */
    public String generate(int length, boolean includeUpper, boolean includeLower, boolean includeDigits, boolean includeSymbols) {
        StringBuilder pool = new StringBuilder();

        if (includeUpper) pool.append(UPPERCASE);
        if (includeLower) pool.append(LOWERCASE);
        if (includeDigits) pool.append(DIGITS);
        if (includeSymbols) pool.append(SYMBOLS);

        if (pool.length() == 0) {
            throw new IllegalArgumentException("At least one character type must be included.");
        }

        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(pool.length());
            result.append(pool.charAt(index));
        }

        return result.toString();
    }

    /**
     * Generates a secure password with a default set of options.
     *
     * @param length Length of the password.
     * @return A secure password.
     */
    public String generatePassword(int length) {
        return generate(length, true, true, true, true); // Include all character types
    }
}


