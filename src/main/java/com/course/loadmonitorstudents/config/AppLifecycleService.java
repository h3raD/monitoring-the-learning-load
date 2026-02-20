package com.course.loadmonitorstudents.config;

import com.course.loadmonitorstudents.service.TelegramBotStarter;
import com.course.loadmonitorstudents.service.TelegramNotificationService;

public class AppLifecycleService {
    private static volatile boolean isInitialized = false;

    /**
     * Инициализирует телеграм сервисы приложения.
     * Запускает Telegram бот и фоновые мониторинг процессы.
     */
    public static synchronized void initialize() {
        if (!isInitialized) {
            System.out.println(" Инициализация Telegram сервисов...");

            TelegramBotStarter.startBot();

            TelegramNotificationService.getInstance().startBackgroundMonitoring();

            isInitialized = true;
            System.out.println(" Telegram сервисы инициализированы");
        }
    }

    /**
     * Останавливает все Telegram сервисы приложения.
     */
    public static synchronized void shutdown() {
        if (isInitialized) {
            System.out.println(" Остановка Telegram сервисов...");

            TelegramNotificationService.getInstance().stop();
            TelegramBotStarter.stopBot();

            isInitialized = false;
            System.out.println(" Telegram сервисы остановлены");
        }
    }
}