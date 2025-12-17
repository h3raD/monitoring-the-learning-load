package com.course.loadmonitorstudents.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchToAuthScene() {
        loadScene("/com/course/loadmonitorstudents/gui/authorization.fxml", "Авторизация");
    }

    public static void switchToRegScene() {
        loadScene("/com/course/loadmonitorstudents/gui/registration.fxml", "Регистрация");
    }

    public static void switchToCuratorDashboard() {
        loadScene("/com/course/loadmonitorstudents/gui/curator-dashboard.fxml", "Панель куратора");
    }

    public static void switchToStudentDashboard() {
        loadScene("/com/course/loadmonitorstudents/gui/student-dashboard.fxml", "Панель студента");
    }

    private static void loadScene(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    SceneManager.class.getResource(fxmlPath)
            ));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Ошибка загрузки сцены: " + fxmlPath);
            e.printStackTrace();
        }
    }
}