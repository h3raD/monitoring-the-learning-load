package com.course.loadmonitorstudents.config;

import com.course.loadmonitorstudents.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationConfig {
    private static User currentUser;
    private static final Properties properties = new Properties();
    
    static {
        try (InputStream input = ApplicationConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                System.out.println("ОШИБКА: Файл application.properties не найден!");
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
        }
    }

    private ApplicationConfig() {

    }

    /**
     * Настраивает текущего авторизованного пользователя.
     *
     * @param user текущий авторизованный пользователь
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Возвращает текущего авторизованного пользователя.
     *
     * @return текущий пользователь или null
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Получает ID текущего пользователя.
     *
     * @return ID текущего пользователя или null
     */
    public static Long getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    /**
     * Получает токен Telegram бота из конфигурации.
     *
     * @return токен бота
     */
    public static String getTelegramBotToken() {
        return properties.getProperty("telegram.bot.token", "");
    }

    /**
     * Получает ID Google Calendar из конфигурации.
     *
     * @return Google Calendar ID
     */
    public static String getGoogleCalendarId() {
        return properties.getProperty("google.calendar.id", "");
    }
}
