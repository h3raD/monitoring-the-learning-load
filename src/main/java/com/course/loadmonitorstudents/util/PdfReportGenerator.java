package com.course.loadmonitorstudents.util;

import com.course.loadmonitorstudents.dto.TaskWithStudentDTO;
import com.course.loadmonitorstudents.model.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static void generateTaskReport(List<TaskWithStudentDTO> tasks,
                                          String filterType,
                                          boolean descending,
                                          String filePath) throws IOException {

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = null;

            try {
                contentStream = new PDPageContentStream(document, page);
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float yPosition = yStart;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Отчет по задачам");
                contentStream.endText();

                yPosition -= 30;

                contentStream.setFont(PDType1Font.HELVETICA, 10);
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

                drawTableHeader(contentStream, margin, yPosition, tableWidth, headers, columnWidths);
                yPosition -= 20;

                contentStream.setFont(PDType1Font.HELVETICA, 8);
                for (TaskWithStudentDTO task : tasks) {
                    if (yPosition < 100) {
                        // Закрываем текущий contentStream
                        contentStream.close();
                        // Создаем новую страницу
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        // Создаем новый contentStream для новой страницы
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = yStart - 50;
                        contentStream.setFont(PDType1Font.HELVETICA, 8);
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

                    drawTableRow(contentStream, margin, yPosition, tableWidth, rowData, columnWidths);
                    yPosition -= 15;
                }

                yPosition -= 20;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Всего задач: " + tasks.size());
                contentStream.endText();

            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }

            document.save(filePath);
        }
    }

    public static void generateUserReport(List<User> users, String filePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = null;

            try {
                contentStream = new PDPageContentStream(document, page);
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float yPosition = yStart;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Отчет по пользователям (без паролей)");
                contentStream.endText();

                yPosition -= 30;

                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Дата генерации: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
                contentStream.endText();

                yPosition -= 30;

                String[] headers = {"ID", "Email", "Имя", "Фамилия", "Роль", "Куратор ID"};
                float[] columnWidths = {30, 120, 80, 80, 60, 60};

                drawTableHeader(contentStream, margin, yPosition, tableWidth, headers, columnWidths);
                yPosition -= 20;

                contentStream.setFont(PDType1Font.HELVETICA, 8);
                for (User user : users) {
                    if (yPosition < 100) {
                        // Закрываем текущий и создаем новый для новой страницы
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = yStart - 50;
                        contentStream.setFont(PDType1Font.HELVETICA, 8);
                    }

                    String[] rowData = {
                            String.valueOf(user.getId()),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getRole().toString(),
                            user.getCuratorId() != null ? String.valueOf(user.getCuratorId()) : ""
                    };

                    drawTableRow(contentStream, margin, yPosition, tableWidth, rowData, columnWidths);
                    yPosition -= 15;
                }

                yPosition -= 20;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Всего пользователей: " + users.size());
                contentStream.endText();

            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }

            document.save(filePath);
        }
    }

    private static void drawTableHeader(PDPageContentStream contentStream, float x, float y,
                                        float width, String[] headers, float[] columnWidths)
            throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
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

    private static void drawTableRow(PDPageContentStream contentStream, float x, float y,
                                     float width, String[] rowData, float[] columnWidths)
            throws IOException {
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