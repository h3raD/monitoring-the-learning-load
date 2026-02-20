package com.course.loadmonitorstudents;

import javafx.application.Application;

public class Launcher {
    /**
     * Точка входа в приложение.
     * Запускает JavaFX приложение через класс LoadMonitor.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        Application.launch(LoadMonitor.class, args);
    }
}
