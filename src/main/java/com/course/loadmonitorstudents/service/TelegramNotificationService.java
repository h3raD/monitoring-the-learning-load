package com.course.loadmonitorstudents.service;

import com.course.loadmonitorstudents.dao.UserDAO;
import com.course.loadmonitorstudents.dao.db.UserDAOPSql;
import com.course.loadmonitorstudents.model.Task;
import com.course.loadmonitorstudents.model.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TelegramNotificationService {
    private static final String BOT_TOKEN = "8063383111:AAF6_U_WhyAgOd6-lF_xrT6ua-8f8GieCWw";

    private static TelegramNotificationService instance;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean isRunning = false;
    private final UserDAO userDAO = new UserDAOPSql();

    private TelegramNotificationService() {}

    public static TelegramNotificationService getInstance() {
        if (instance == null) {
            instance = new TelegramNotificationService();
        }
        return instance;
    }

    public void sendNotificationToStudent(Long studentId, String message) {
        try {
            System.out.println("📤 Отправка уведомления студенту ID=" + studentId);
            User student = userDAO.findById(studentId);

            if (student == null) {
                System.err.println("❌ Студент не найден: " + studentId);
                return;
            }

            Long telegramId = student.getTelegramID();
            if (telegramId == null) {
                System.err.println("❌ У студента " + studentId + " не указан Telegram ID");
                return;
            }

            System.out.println("👤 Студент: " + student.getFirstName() + " " + student.getLastName());
            System.out.println("💬 Telegram ID: " + telegramId);
            System.out.println("📝 Сообщение: " + message.substring(0, Math.min(100, message.length())) + "...");

            sendHtmlMessage(telegramId, message);

        } catch (SQLException e) {
            System.err.println("❌ Ошибка БД при отправке уведомления: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Ошибка отправки уведомления: " + e.getMessage());
        }
    }


    public void notifyNewTask(Task task) {
        String message = String.format(
                "<b>📌 Новая задача</b>%n%n" +
                        "<b>📝 Название:</b> %s%n" +
                        "<b>📋 Описание:</b> %s%n" +
                        "<b>⏰ Дедлайн:</b> %s%n" +
                        "<b>🏷️ Статус:</b> %s%n%n" +
                        "Для начала работы перейдите в приложение.",
                escapeHtml(task.getTitle()),
                escapeHtml(task.getDescription()),
                formatDateTime(task.getDeadline()),
                task.getStatus()
        );

        System.out.println("🎯 Отправка уведомления о новой задаче студенту ID=" + task.getStudentId());
        sendNotificationToStudent(task.getStudentId(), message);
    }

    public void notifyDeadlineApproaching(Task task) {
        String message = String.format(
                "<b>⚠️ Скоро дедлайн!</b>%n%n" +
                        "<b>📝 Задача:</b> %s%n" +
                        "<b>⏰ Дедлайн:</b> %s%n" +
                        "<b>⏳ Осталось меньше 24 часов</b>%n%n" +
                        "Успейте завершить задачу вовремя!",
                escapeHtml(task.getTitle()),
                formatDateTime(task.getDeadline())
        );

        sendNotificationToStudent(task.getStudentId(), message);
    }

    public void notifySleepRecommendation(Long studentId, int sleepHours) {
        String recommendation;
        if (sleepHours < 6) {
            recommendation = "⚠️ <b>Внимание!</b> Вы спите менее 6 часов. Рекомендуется увеличить время сна для поддержания продуктивности.";
        } else if (sleepHours > 9) {
            recommendation = "ℹ️ Вы спите более 9 часов. Возможно, стоит сократить время сна для лучшего самочувствия.";
        } else {
            recommendation = "✅ Отличный результат! Вы соблюдаете рекомендуемую норму сна (7-8 часов).";
        }

        String message = String.format(
                "<b>😴 Рекомендация по сну</b>%n%n" +
                        "<b>📊 Ваши часы сна:</b> %d часов%n" +
                        "<b>💡 Рекомендация:</b> %s%n%n" +
                        "Здоровый сон = продуктивная учёба!",
                sleepHours, recommendation
        );

        sendNotificationToStudent(studentId, message);
    }

    private void sendHtmlMessage(Long chatId, String htmlText) {
        try {
            String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";

            String jsonText = htmlText
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");

            String jsonPayload = String.format(
                    "{\"chat_id\":%d,\"text\":\"%s\",\"parse_mode\":\"HTML\"}",
                    chatId, jsonText
            );

            System.out.println("Отправка в Telegram:");
            System.out.println("URL: " + url);
            System.out.println("Chat ID: " + chatId);
            System.out.println("Payload (первые 200 символов): " +
                    jsonPayload.substring(0, Math.min(200, jsonPayload.length())) + "...");

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes("UTF-8"));
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("📡 Код ответа Telegram API: " + responseCode);

            if (responseCode == 200) {
                System.out.println("Сообщение успешно отправлено");
            } else {
                System.err.println("Ошибка Telegram API: " + responseCode);

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    System.err.println("❌ Ошибка детали: " + errorResponse.toString());

                    if (responseCode == 400) {
                        System.out.println("🔄 Пробуем отправить как простой текст...");
                        sendPlainMessage(chatId, stripHtml(htmlText));
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }


    private void sendPlainMessage(Long chatId, String plainText) {
        try {
            String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";

            String jsonText = plainText
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");

            String jsonPayload = String.format(
                    "{\"chat_id\":%d,\"text\":\"%s\"}",
                    chatId, jsonText
            );

            System.out.println("🔄 Отправка простого текста...");

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes("UTF-8"));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println(" Простое сообщение отправлено");
            } else {
                System.err.println(" Ошибка простого сообщения: " + responseCode);
            }

        } catch (Exception e) {
            System.err.println(" Ошибка отправки простого сообщения: " + e.getMessage());
        }
    }

    private String stripHtml(String html) {
        if (html == null) return "";
        return html
                .replace("<b>", "")
                .replace("</b>", "")
                .replace("<br>", "\n")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("%n", "\n");
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Не указано";
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public void registerStudentChatId(Long studentId, Long chatId) {
        try {
            User student = userDAO.findById(studentId);
            if (student != null) {
                student.setTelegramID(chatId);
                userDAO.update(student);
                System.out.println("✅ Chat ID зарегистрирован для студента: " + studentId);

                sendHtmlMessage(chatId,
                        "<b>👋 Привет! Твой аккаунт успешно подключен к системе уведомлений.</b>%n%n" +
                                "Теперь ты будешь получать:%n" +
                                "📌 Уведомления о новых задачах%n" +
                                "⏰ Напоминания о дедлайнах%n" +
                                "😴 Рекомендации по сну%n%n" +
                                "Удачи в учёбе! 🎓"
                );
            }
        } catch (Exception e) {
            System.err.println("❌ Ошибка регистрации chat ID: " + e.getMessage());
        }
    }

    public void startBackgroundMonitoring() {
        if (isRunning) {
            return;
        }

        isRunning = true;

        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("⏰ Проверка дедлайнов...");
            } catch (Exception e) {
                System.err.println("Ошибка в фоновом мониторинге: " + e.getMessage());
            }
        }, 0, 30, TimeUnit.MINUTES);

        System.out.println("✅ Telegram мониторинг запущен");
    }

    public void stop() {
        isRunning = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        System.out.println("🛑 Telegram мониторинг остановлен");
    }
}