package com.course.loadmonitorstudents.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Checker {
    /**
     * Проверяет валидность email адреса.
     * Проверяет формат, наличие двойных точек, пробелов и длину.
     *
     * @param email проверяемый email адрес
     * @return true если email валиден, false если не соответствует требованиям
     */
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

    /**
     * Проверяет, содержит ли строка только буквы (без цифр и пробелов).
     * Поддерживает как латинский, так и кириллический алфавит.
     *
     * @param text проверяемая строка
     * @return true если строка содержит только буквы
     */
    public static boolean noSpacesNoDigits(String text) {
        return text.matches("^[A-Za-zА-Яа-яЁё]+$");
    }

    /**
     * Проверяет, содержит ли пароль только буквы и цифры (без пробелов).
     *
     * @param password проверяемый пароль
     * @return true если пароль содержит только латинские буквы и цифры
     */
    public static boolean isAlphanumericNoSpaces(String password) {
        return password.matches("^[A-Za-z0-9]+$");
    }

    /**
     * Проверяет, является ли строка валидным неотрицательным числовым ID.
     *
     * @param id проверяемый идентификатор
     * @return true если ID - неотрицательное число
     */
    public static boolean isId(String id) {
        try {
            int number = Integer.parseInt(id);
            return number >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Проверяет, может ли строка быть распарсена в дату с использованием указанного формата.
     *
     * @param deadline строка с датой для проверки
     * @param formatter формат даты для парсинга
     * @return true если дата валидна и может быть распарсена
     */
    public static boolean checkDate(String deadline, DateTimeFormatter formatter) {
        try {
            LocalDateTime.parse(deadline, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
