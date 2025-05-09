package com.savingsgroup.authservice.util;


import java.util.regex.Pattern;

/**
 * Utility class for common validation methods used throughout the application.
 */
public class ValidationUtil {

    // Email validation regex pattern
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    // Password validation regex pattern (at least 8 chars, 1 uppercase, 1 lowercase, 1 number)
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

    // Phone number validation regex pattern (supports international format)
    private static final String PHONE_PATTERN = "^\\+?[0-9]{10,15}$";

    // Username validation regex pattern (alphanumeric, underscore, 3-20 chars)
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,20}$";

    // Compiled patterns for better performance
    private static final Pattern emailRegex = Pattern.compile(EMAIL_PATTERN);
    private static final Pattern passwordRegex = Pattern.compile(PASSWORD_PATTERN);
    private static final Pattern phoneRegex = Pattern.compile(PHONE_PATTERN);
    private static final Pattern usernameRegex = Pattern.compile(USERNAME_PATTERN);

    /**
     * Validates if the provided email string matches email format.
     *
     * @param email email string to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return emailRegex.matcher(email).matches();
    }

    /**
     * Validates if the provided password meets the required strength criteria.
     *
     * @param password password string to validate
     * @return true if password is valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return passwordRegex.matcher(password).matches();
    }

    /**
     * Validates if the provided phone number is in a valid format.
     *
     * @param phoneNumber phone number string to validate
     * @return true if phone number is valid, false otherwise
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return phoneRegex.matcher(phoneNumber).matches();
    }

    /**
     * Validates if the provided username meets the required format criteria.
     *
     * @param username username string to validate
     * @return true if username is valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return usernameRegex.matcher(username).matches();
    }

    /**
     * Validates if the provided string has at least the specified minimum length.
     *
     * @param value string to validate
     * @param minLength minimum required length
     * @return true if string length is valid, false otherwise
     */
    public static boolean hasMinimumLength(String value, int minLength) {
        if (value == null) {
            return false;
        }
        return value.length() >= minLength;
    }

    /**
     * Validates if the provided string does not exceed the specified maximum length.
     *
     * @param value string to validate
     * @param maxLength maximum allowed length
     * @return true if string length is valid, false otherwise
     */
    public static boolean hasMaximumLength(String value, int maxLength) {
        if (value == null) {
            return true; // null values will be caught by other validators if required
        }
        return value.length() <= maxLength;
    }
}