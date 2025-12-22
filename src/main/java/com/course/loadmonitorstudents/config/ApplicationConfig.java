package com.course.loadmonitorstudents.config;

import com.course.loadmonitorstudents.model.User;

public class ApplicationConfig {
    private static User currentUser;
    public final static String googleindent = "d28d5e5db4dac173ffff8bfc20d94ad7d6b29e11846719fd41e12fac783ada46@group.calendar.google.com";

    private ApplicationConfig() {

    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static Long getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}
