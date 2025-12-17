package com.course.loadmonitorstudents.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Checker {
    public static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (!email.matches(regex))
            return false;

        if (email.contains(".."))
            return false;
        if (email.startsWith(".") || email.endsWith("."))
            return false;
        if (email.contains(" "))
            return false;

        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];

        if (domain.startsWith("-") || domain.endsWith("-"))
            return false;

        if (local.length() > 64)
            return false;

        if (email.length() > 254)
            return false;

        return true;
    }

    public static boolean noSpacesNoDigits(String text) {
        return text.matches("^[A-Za-zА-Яа-яЁё]+$");
    }

    public static boolean isAlphanumericNoSpaces(String password) {
        return password.matches("^[A-Za-z0-9]+$");
    }

    public static boolean isId(String id) {
        try {
            int number = Integer.parseInt(id);
            return number >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean checkDate(String deadline, DateTimeFormatter formatter) {
        try {
            LocalDateTime.parse(deadline, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
