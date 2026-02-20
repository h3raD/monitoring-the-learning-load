package com.course.loadmonitorstudents.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    /**
     * Хеширует пароль с использованием алгоритма BCrypt.
     *
     * @param password исходный пароль для хеширования
     * @return захешированный пароль
     * @throws IllegalArgumentException если пароль равен null или пуст
     */
    public static String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Проверяет соответствие пароля его хешу из базы данных.
     *
     * @param password проверяемый пароль
     * @param hashedPassword хешированный пароль из базы данных
     * @return true если пароль верный, false если нет или произошла ошибка
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}