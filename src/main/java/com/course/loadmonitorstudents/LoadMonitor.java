package com.course.loadmonitorstudents;

import com.course.loadmonitorstudents.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class LoadMonitor extends Application {
    /**
     * Инициализирует главное окно приложения с параметрами.
     * Устанавливает минимальный размер и переключает сцену на авторизацию.
     *
     * @param primaryStage главная сцена приложения
     * @throws IOException если произойдет ошибка при загрузке сцены
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);
        primaryStage.setResizable(false);
        SceneManager.setPrimaryStage(primaryStage);
        SceneManager.switchToAuthScene();
        primaryStage.show();
    }
}
