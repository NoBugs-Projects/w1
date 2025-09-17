package com.teamcity.api.generators;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Utility class for generating random test data.
 * <p>
 * This class provides methods for generating random string values that are suitable
 * for use in test data. All generated strings are prefixed with "test_" to make
 * them easily identifiable as test data in logs and reports.
 * </p>
 *
 * <p>
 * The class uses Apache Commons Lang's RandomStringUtils for secure random string
 * generation, ensuring that generated values are cryptographically secure and
 * suitable for testing purposes.
 * </p>
 *
 * @author TeamCity Testing Framework
 * @version 1.0
 * @since 1.0
 * @see RandomStringUtils
 */
public final class RandomData {

    /**
     * Default length for generated random strings.
     * <p>
     * This constant defines the default length of random alphabetic characters
     * that will be generated (excluding the "test_" prefix).
     * </p>
     */
    private static final int LENGTH = 10;

    /**
     * Prefix added to all generated random strings.
     * <p>
     * This prefix helps identify generated test data in logs and reports,
     * making it easier to distinguish between test data and real data.
     * </p>
     */
    private static final String TEST_PREFIX = "test_";

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This utility class should not be instantiated as all methods are static.
     * </p>
     */
    private RandomData() {
    }

    /**
     * Generates a random string with the default length.
     * <p>
     * This method generates a random alphabetic string with the default length
     * (10 characters) plus the "test_" prefix, resulting in a total length of 15 characters.
     * </p>
     *
     * @return a random string prefixed with "test_"
     */
    public static String getString() {
        return TEST_PREFIX + RandomStringUtils.secure().nextAlphabetic(LENGTH);
    }

    /**
     * Generates a random string with the specified length.
     * <p>
     * This method generates a random alphabetic string with the specified length
     * plus the "test_" prefix. The minimum length is guaranteed to be 10 characters
     * (excluding the prefix) to ensure sufficient randomness.
     * </p>
     *
     * @param length the desired length of the random part (excluding prefix)
     * @return a random string prefixed with "test_"
     */
    public static String getString(int length) {
        // Генерируем строку кастомной длины, учитывая то, что test_ уже занимает 5 символов.
        // Строка меньше 10 символов не может получиться
        return TEST_PREFIX + RandomStringUtils.secure().nextAlphabetic(Math.max(length - TEST_PREFIX.length(), LENGTH));
    }

}
