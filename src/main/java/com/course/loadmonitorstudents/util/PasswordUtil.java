package com.course.loadmonitorstudents.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    /**
     * Хеширует пароль с использованием BCrypt
     * @param password - исходный пароль
     * @return захешированный пароль
     */
    public static String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Проверяет соответствие пароля хешу из БД
     * @param password - проверяемый пароль
     * @param hashedPassword - хеш из базы данных
     * @return true если пароль верный
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