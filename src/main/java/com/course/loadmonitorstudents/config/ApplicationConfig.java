package com.course.loadmonitorstudents.config;

import com.course.loadmonitorstudents.model.User;

public class ApplicationConfig {
    private static User currentUser;

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
