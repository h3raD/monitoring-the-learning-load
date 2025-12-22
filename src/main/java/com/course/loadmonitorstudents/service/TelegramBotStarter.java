package com.course.loadmonitorstudents.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TelegramBotStarter {
    private static final String BOT_TOKEN = "8063383111:AAF6_U_WhyAgOd6-lF_xrT6ua-8f8GieCWw";
    private static volatile boolean running = true;

    public static void startBot() {
        Thread botThread = new Thread(() -> {
            System.out.println("Telegram бот запускается...");
            int lastUpdateId = 0;

            System.out.println("Telegram бот запущен");

            while (running) {
                try {
                    String response = getUpdates(lastUpdateId + 1);

                    if (response != null && response.contains("\"ok\":true") && response.contains("\"result\":")) {
                        int resultStart = response.indexOf("\"result\":") + 9;
                        int resultEnd = response.indexOf("]", resultStart) + 1;

                        if (resultStart > 9 && resultEnd > resultStart) {
                            String resultStr = response.substring(resultStart, resultEnd);

                            int messageStart = 0;
                            while ((messageStart = resultStr.indexOf("\"message\":", messageStart)) != -1) {
                                int updateIdStart = resultStr.lastIndexOf("\"update_id\":", messageStart);
                                if (updateIdStart != -1) {
                                    updateIdStart += 12;
                                    int updateIdEnd = resultStr.indexOf(",", updateIdStart);
                                    if (updateIdEnd > updateIdStart) {
                                        try {
                                            lastUpdateId = Integer.parseInt(
                                                    resultStr.substring(updateIdStart, updateIdEnd).trim()
                                            );
                                        } catch (NumberFormatException ignored) {
                                        }
                                    }
                                }

                                int chatIdStart = resultStr.indexOf("\"chat\":{\"id\":", messageStart);
                                if (chatIdStart != -1) {
                                    chatIdStart += 13;
                                    int chatIdEnd = resultStr.indexOf(",", chatIdStart);
                                    if (chatIdEnd > chatIdStart) {
                                        String chatIdStr = resultStr.substring(chatIdStart, chatIdEnd).trim();

                                        int textStart = resultStr.indexOf("\"text\":\"", messageStart);
                                        if (textStart != -1) {
                                            textStart += 8;
                                            int textEnd = resultStr.indexOf("\"", textStart);
                                            if (textEnd > textStart) {
                                                String text = resultStr.substring(textStart, textEnd);

                                                try {
                                                    Long chatId = Long.parseLong(chatIdStr);
                                                    System.out.println("📨 Получено сообщение: " + text + " от " + chatId);

                                                    if (text.startsWith("/start")) {
                                                        String responseText =
                                                                "👋 *Привет!*\n\n" +
                                                                        "🤖 *Я — бот системы мониторинга нагрузки*\n\n" +
                                                                        "📋 *Твой Chat ID:* `" + chatId + "`\n\n" +
                                                                        "🔧 *Чтобы подключить уведомления:*\n" +
                                                                        "1. Открой приложение\n" +
                                                                        "2. Перейди в Настройки\n" +
                                                                        "3. Вставь этот Chat ID\n" +
                                                                        "4. Нажми Привязать\n\n" +
                                                                        "📌 *Будешь получать:*\n" +
                                                                        "• Новые задачи\n" +
                                                                        "• Напоминания\n" +
                                                                        "• Рекомендации по сну\n\n" +
                                                                        "Удачи в учёбе! 🎓";

                                                        sendSimpleMessage(chatId, responseText);
                                                    }
                                                } catch (NumberFormatException e) {
                                                    System.err.println("❌ Неверный chat_id: " + chatIdStr);
                                                }
                                            }
                                        }
                                    }
                                }

                                messageStart += 10;
                            }
                        }
                    }

                    Thread.sleep(3000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("⚠️ Ошибка бота: " + e.getMessage());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            System.out.println("🛑 Telegram бот остановлен");
        });

        botThread.setDaemon(true);
        botThread.start();
    }

    private static String getUpdates(int offset) {
        try {
            String urlString = String.format(
                    "https://api.telegram.org/bot%s/getUpdates?timeout=10&offset=%d",
                    BOT_TOKEN, offset
            );

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            return response.toString();

        } catch (Exception e) {
            System.err.println("Ошибка получения обновлений: " + e.getMessage());
            return null;
        }
    }

    private static void sendSimpleMessage(Long chatId, String text) {
        try {
            String urlString = String.format(
                    "https://api.telegram.org/bot%s/sendMessage",
                    BOT_TOKEN
            );

            String escapedText = text.replace("\"", "\\\"");
            String payload = String.format(
                    "{\"chat_id\":%d,\"text\":\"%s\",\"parse_mode\":\"Markdown\"}",
                    chatId, escapedText
            );

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            conn.getOutputStream().write(payload.getBytes());

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Сообщение отправлено");
            } else {
                System.err.println("Ошибка: " + responseCode);
            }

        } catch (Exception e) {
            System.err.println("Ошибка отправки: " + e.getMessage());
        }
    }

    public static void stopBot() {
        running = false;
    }
}