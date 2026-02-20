package com.course.loadmonitorstudents.service;

import com.course.loadmonitorstudents.config.ApplicationConfig;
import com.course.loadmonitorstudents.dao.RestDAO;
import com.course.loadmonitorstudents.dao.UserDAO;
import com.course.loadmonitorstudents.dao.db.RestDAOPSql;
import com.course.loadmonitorstudents.dao.db.UserDAOPSql;
import com.course.loadmonitorstudents.model.Task;
import com.course.loadmonitorstudents.model.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TelegramNotificationService {
    private static final String BOT_TOKEN = ApplicationConfig.getTelegramBotToken();
    private static final int DAILY_SLEEP_NORM_MIN = 7;
    private static final int DAILY_SLEEP_NORM_MAX = 8;

    private static TelegramNotificationService instance;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean isRunning = false;
    private final UserDAO userDAO = new UserDAOPSql();
    private final RestDAO restDAO = new RestDAOPSql();

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
        if (sleepHours < DAILY_SLEEP_NORM_MIN) {
            recommendation = "⚠️ <b>Внимание!</b> Вы спали " + sleepHours + " часов. Это меньше нормы (7-8 часов). Рекомендуется увеличить время сна!";
        } else if (sleepHours > DAILY_SLEEP_NORM_MAX) {
            recommendation = "ℹ️ Вы спали " + sleepHours + " часов. Это больше нормы (7-8 часов). Рекомендуется немного сократить время сна.";
        } else {
            recommendation = "✅ Отличный результат! Вы спали " + sleepHours + " часов - это идеальная норма! 🎉";
        }

        String message = String.format(
                "<b>😴 Статистика сна</b>%n%n" +
                        "<b>📊 Сегодняшний сон:</b> %d часов%n" +
                        "<b>💡 Оценка:</b> %s%n%n" +
                        "Помните: здоровый сон - залог продуктивности!",
                sleepHours, recommendation
        );

        sendNotificationToStudent(studentId, message);
        
        sendWeeklySleepStats(studentId);
    }

    private void sendWeeklySleepStats(Long studentId) {
        try {
            List<com.course.loadmonitorstudents.model.Rest> allRests = restDAO.findByStudentId(studentId);
            
            if (allRests == null || allRests.isEmpty()) {
                return;
            }

            java.util.Collections.sort(allRests, (a, b) -> b.getDate().compareTo(a.getDate()));

            LocalDate today = LocalDate.now();
            com.course.loadmonitorstudents.model.Rest todayRest = null;
            
            for (com.course.loadmonitorstudents.model.Rest rest : allRests) {
                if (rest.getDate().equals(today)) {
                    todayRest = rest;
                    break;
                }
            }

            if (todayRest != null) {
                int sleepHours = todayRest.getHours();
                String dayInfo;
                
                if (sleepHours < DAILY_SLEEP_NORM_MIN) {
                    dayInfo = String.format(
                        "⚠️ <b>Вы спали МАЛО в этот день!</b>%n" +
                        "📅 Дата: %s%n" +
                        "😴 Часов сна: %d (норма: 7-8)%n%n",
                        today, sleepHours
                    );
                } else if (sleepHours > DAILY_SLEEP_NORM_MAX) {
                    dayInfo = String.format(
                        "ℹ️ Вы спали МНОГО в этот день%n" +
                        "📅 Дата: %s%n" +
                        "😴 Часов сна: %d (норма: 7-8)%n%n",
                        today, sleepHours
                    );
                } else {
                    dayInfo = String.format(
                        "✅ <b>Вы спали НОРМАЛЬНО в этот день!</b>%n" +
                        "📅 Дата: %s%n" +
                        "😴 Часов сна: %d (норма: 7-8) 🎉%n%n",
                        today, sleepHours
                    );
                }

                String message = String.format(
                    "<b>😴 Статистика сна на сегодня</b>%n%n%s" +
                    "<i>Здоровый сон = продуктивная учёба!</i>",
                    dayInfo
                );

                sendNotificationToStudent(studentId, message);
            }

            if (allRests.size() < 7) {
                return;
            }

            java.util.List<com.course.loadmonitorstudents.model.Rest> last7Days = new java.util.ArrayList<>();
            LocalDate checkDate = today;

            for (int i = 0; i < 7; i++) {
                boolean found = false;
                for (com.course.loadmonitorstudents.model.Rest rest : allRests) {
                    if (rest.getDate().equals(checkDate)) {
                        last7Days.add(rest);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return;
                }
                checkDate = checkDate.minusDays(1);
            }

            int totalSleep = 0;
            for (com.course.loadmonitorstudents.model.Rest rest : last7Days) {
                totalSleep += rest.getHours();
            }

            double avgSleep = (double) totalSleep / 7;

            String weeklyRecommendation;
            String emoji;
            
            if (avgSleep < DAILY_SLEEP_NORM_MIN) {
                emoji = "⚠️";
                weeklyRecommendation = String.format(
                    "<b>ВЫ НЕ СПИТЕ НОРМАЛЬНО!</b> За всю неделю вы спали в среднем всего %.1f часов/сутки. " +
                    "Норма: 7-8 часов. Срочно добавьте сна!", 
                    avgSleep
                );
            } else if (avgSleep > DAILY_SLEEP_NORM_MAX) {
                emoji = "ℹ️";
                weeklyRecommendation = String.format(
                    "За всю неделю вы спали в среднем %.1f часов/сутки - это больше нормы (7-8 часов). " +
                    "Постарайтесь немного сократить.", 
                    avgSleep
                );
            } else {
                emoji = "✅";
                weeklyRecommendation = String.format(
                    "<b>Отлично!</b> За всю неделю вы спали нормально - в среднем %.1f часов/сутки. " +
                    "Продолжайте в том же духе! 🎯", 
                    avgSleep
                );
            }

            String message = String.format(
                    "<b>%s 📊 Статистика сна за неделю</b>%n%n" +
                            "<b>Всего часов:</b> %d часов за 7 суток%n" +
                            "<b>Среднее в сутки:</b> %.1f часов%n" +
                            "<b>Норма:</b> 7-8 часов/сутки%n%n" +
                            "<b>📈 Оценка:</b> %s%n%n" +
                            "<i>Здоровый сон - залог успеха! 💪</i>",
                    emoji, totalSleep, avgSleep, weeklyRecommendation
            );

            Thread.sleep(1000);
            sendNotificationToStudent(studentId, message);

        } catch (SQLException e) {
            System.err.println("❌ Ошибка БД при получении данных сна: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("❌ Ошибка при отправке статистики сна: " + e.getMessage());
        }
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