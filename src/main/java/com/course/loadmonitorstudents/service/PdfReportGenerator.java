package com.course.loadmonitorstudents.service;

import com.course.loadmonitorstudents.dto.TaskWithStudentDTO;
import com.course.loadmonitorstudents.model.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /**
     * Загружает кириллический шрифт для PDF.
     *
     * @param document PDF документ
     * @return матрица шрифта
     * @throws IOException если не удался загрузить шрифт
     */
    private static PDFont loadCyrillicFont(PDDocument document) throws IOException {
        try {
            InputStream fontStream = null;

            fontStream = PdfReportGenerator.class
                    .getResourceAsStream("/com/course/loadmonitorstudents/fonts/arialmt.ttf");

            if (fontStream == null) {
                fontStream = PdfReportGenerator.class
                        .getClassLoader()
                        .getResourceAsStream("com/course/loadmonitorstudents/fonts/arialmt.ttf");
            }

            if (fontStream == null) {
                fontStream = Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("fonts/arialmt.ttf");
            }

            if (fontStream != null) {
                PDType0Font font = PDType0Font.load(document, fontStream);
                System.out.println("Шрифт arialmt.ttf успешно загружен");
                return font;
            } else {
                throw new IOException("Шрифт arialmt.ttf не найден в ресурсах");
            }

        } catch (Exception e) {
            System.err.println("КРИТИЧЕСКАЯ ОШИБКА: Не удалось загрузить кириллический шрифт!");
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Не удалось загрузить кириллический шрифт. Убедитесь, что файл arialmt.ttf находится в resources/fonts/", e);
        }
    }

    /**
     * Генерирует PDF отчет по задачам.
     *
     * @param tasks лист задач
     * @param filterType тип фильтрации
     * @param descending если true — сортировка по убыванию
     * @param filePath путь к экспортированному файлу
     * @throws IOException если ошибка генерации PDF
     */
    public static void generateTaskReport(List<TaskWithStudentDTO> tasks,
                                          String filterType,
                                          boolean descending,
                                          String filePath) throws IOException {

        try (PDDocument document = new PDDocument()) {
            PDFont font = loadCyrillicFont(document);

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = null;

            try {
                contentStream = new PDPageContentStream(document, page);
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float yPosition = yStart;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;

                contentStream.setFont(font, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Отчет по задачам");
                contentStream.endText();

                yPosition -= 30;

                contentStream.setFont(font, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                String filterInfo = "Фильтр: " +
                        (filterType != null ? filterType : "Без фильтра") +
                        " | Сортировка: " + (descending ? "по убыванию" : "по возрастанию");
                contentStream.showText(filterInfo);
                contentStream.endText();

                yPosition -= 20;

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Дата генерации: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
                contentStream.endText();

                yPosition -= 30;

                String[] headers = {"ID", "Название", "Описание", "Статус", "Дедлайн",
                        "Студент", "ID студента"};
                float[] columnWidths = {30, 80, 100, 60, 80, 100, 50};

                drawTableHeader(contentStream, font, margin, yPosition, tableWidth, headers, columnWidths);
                yPosition -= 20;

                contentStream.setFont(font, 8);
                for (TaskWithStudentDTO task : tasks) {
                    if (yPosition < 100) {
                        try {
                            contentStream.endText();
                        } catch (Exception ignored) {}
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = yStart - 50;
                        contentStream.setFont(font, 8);
                    }

                    String[] rowData = {
                            String.valueOf(task.getId()),
                            task.getTitle(),
                            task.getDescription(),
                            task.getStatus().toString(),
                            task.getDeadline() != null ?
                                    task.getDeadline().format(DATE_FORMATTER) : "",
                            task.getStudentLastName() + " " + task.getStudentFirstName(),
                            String.valueOf(task.getStudentId())
                    };

                    drawTableRow(contentStream, font, margin, yPosition, tableWidth, rowData, columnWidths);
                    yPosition -= 15;
                }

                yPosition -= 20;
                contentStream.setFont(font, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Всего задач: " + tasks.size());
                contentStream.endText();

            } finally {
                if (contentStream != null) {
                    try {
                        contentStream.endText();
                    } catch (Exception ignored) {}
                    contentStream.close();
                }
            }

            document.save(filePath);
        }
    }

    /**
     * Генерирует PDF отчет по пользователям (без паролей).
     *
     * @param users лист пользователей
     * @param filePath путь к экспортированному файлу
     * @throws IOException если ошибка генерации PDF
     */
    public static void generateUserReport(List<User> users, String filePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDFont font = loadCyrillicFont(document);

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = null;

            try {
                contentStream = new PDPageContentStream(document, page);
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float yPosition = yStart;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;

                contentStream.setFont(font, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Отчет по пользователям (без паролей)");
                contentStream.endText();

                yPosition -= 30;

                contentStream.setFont(font, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Дата генерации: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
                contentStream.endText();

                yPosition -= 30;

                String[] headers = {"ID", "Email", "Имя", "Фамилия", "Роль", "Куратор ID"};
                float[] columnWidths = {30, 120, 80, 80, 60, 60};

                drawTableHeader(contentStream, font, margin, yPosition, tableWidth, headers, columnWidths);
                yPosition -= 20;

                contentStream.setFont(font, 8);
                for (User user : users) {
                    if (yPosition < 100) {
                        try {
                            contentStream.endText();
                        } catch (Exception ignored) {
                        }
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = yStart - 50;
                        contentStream.setFont(font, 8);
                    }

                    String[] rowData = {
                            String.valueOf(user.getId()),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getRole().toString(),
                            user.getCuratorId() != null ? String.valueOf(user.getCuratorId()) : ""
                    };

                    drawTableRow(contentStream, font, margin, yPosition, tableWidth, rowData, columnWidths);
                    yPosition -= 15;
                }

                yPosition -= 20;
                contentStream.setFont(font, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Всего пользователей: " + users.size());
                contentStream.endText();

            } finally {
                if (contentStream != null) {
                    try {
                        contentStream.endText();
                    } catch (Exception ignored) {
                    }
                    contentStream.close();
                }
            }

            document.save(filePath);
        }
    }

    /**
     * Устанавливает заголовок таблицы в PDF.
     *
     * @param contentStream поток содержимого
     * @param font шрифт
     * @param x X-координата
     * @param y Y-координата
     * @param width ширина таблицы
     * @param headers массив заголовков
     * @param columnWidths ширины колонок
     * @throws IOException если ошибка при установке
     */
    private static void drawTableHeader(PDPageContentStream contentStream, PDFont font,
                                        float x, float y, float width,
                                        String[] headers, float[] columnWidths) throws IOException {
        contentStream.setFont(font, 10);
        float currentX = x;

        for (int i = 0; i < headers.length; i++) {
            contentStream.setLineWidth(1f);
            contentStream.addRect(currentX, y - 15, columnWidths[i], 15);
            contentStream.stroke();

            contentStream.beginText();
            contentStream.newLineAtOffset(currentX + 2, y - 12);
            contentStream.showText(headers[i]);
            contentStream.endText();

            currentX += columnWidths[i];
        }
    }

    /**
     * Устанавливает строку таблицы в PDF.
     *
     * @param contentStream поток содержимого
     * @param font шрифт
     * @param x X-координата
     * @param y Y-координата
     * @param width ширина таблицы
     * @param rowData данные строки
     * @param columnWidths ширины колонок
     * @throws IOException если ошибка при рисовке
     */
    private static void drawTableRow(PDPageContentStream contentStream, PDFont font,
                                     float x, float y, float width,
                                     String[] rowData, float[] columnWidths) throws IOException {
        float currentX = x;

        for (int i = 0; i < rowData.length; i++) {
            String cellText = rowData[i];
            if (cellText.length() > 30) {
                cellText = cellText.substring(0, 27) + "...";
            }

            contentStream.setLineWidth(0.5f);
            contentStream.addRect(currentX, y - 12, columnWidths[i], 12);
            contentStream.stroke();

            contentStream.beginText();
            contentStream.newLineAtOffset(currentX + 2, y - 10);
            contentStream.showText(cellText);
            contentStream.endText();

            currentX += columnWidths[i];
        }
    }
}