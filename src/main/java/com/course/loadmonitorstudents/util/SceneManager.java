package com.course.loadmonitorstudents.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class SceneManager {
    private static Stage primaryStage;

    /**
     * Устанавливает примарную сцену приложения.
     *
     * @param stage главная сцена JavaFX приложения
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Переключает сцену приложения на сцену авторизации.
     */
    public static void switchToAuthScene() {
        loadScene("/com/course/loadmonitorstudents/gui/authorization.fxml", "Авторизация");
    }

    /**
     * Переключает сцену приложения на сцену регистрации.
     */
    public static void switchToRegScene() {
        loadScene("/com/course/loadmonitorstudents/gui/registration.fxml", "Регистрация");
    }

    /**
     * Переключает сцену приложения на панель куратора.
     */
    public static void switchToCuratorDashboard() {
        loadScene("/com/course/loadmonitorstudents/gui/curator-dashboard.fxml", "Панель куратора");
    }

    /**
     * Переключает сцену приложения на панель студента.
     */
    public static void switchToStudentDashboard() {
        loadScene("/com/course/loadmonitorstudents/gui/student-dashboard.fxml", "Панель студента");
    }

    /**
     * Загружает сцену из FXML файла и отображает её на главном окне.
     *
     * @param fxmlPath путь к FXML файлу
     * @param title заголовок окна
     */
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
        }
    }
}