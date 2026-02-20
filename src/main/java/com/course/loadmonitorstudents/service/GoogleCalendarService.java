package com.course.loadmonitorstudents.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.course.loadmonitorstudents.config.ApplicationConfig;
import com.course.loadmonitorstudents.model.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import javafx.stage.Modality;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "CourseTP";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static String CALENDAR_ID;
    
    static {
        CALENDAR_ID = ApplicationConfig.getGoogleCalendarId();
    }
    
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_EVENTS);
    private static final String CREDENTIALS_FILE_PATH = "/com/course/loadmonitorstudents/google/secret.json";
    private static final String REDIRECT_URI = "http://localhost";

    private final Calendar calendarService;
    private GoogleClientSecrets clientSecrets;

    /**
     * Конструктор Google Calendar сервиса.
     * Подключается к Google API для работы с календарем.
     *
     * @throws GeneralSecurityException если проблема безопасности
     * @throws IOException если проблема в О/В
     */
    public GoogleCalendarService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        this.clientSecrets = loadClientSecrets();

        Credential credential = getCredentials(HTTP_TRANSPORT, clientSecrets);

        this.calendarService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private GoogleClientSecrets loadClientSecrets() throws IOException {
        InputStream in = getResourceAsStream(CREDENTIALS_FILE_PATH);

        if (in == null) {
            throw new FileNotFoundException("Файл secret.json не найден!");
        }

        GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        if (secrets.getDetails().getClientId() == null) {
            throw new IOException("Файл secret не содержит client_id!");
        }

        return secrets;
    }

    /**
     * Получает удостоверение для Google Calendar.
     *
     * @param HTTP_TRANSPORT новый транспорт
     * @param clientSecrets секреты клиента
     * @return удостоверение
     * @throws IOException если ошибка при авторизации
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT,
                                      GoogleClientSecrets clientSecrets) throws IOException {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        Credential credential = flow.loadCredential("user");

        if (credential != null && credential.getAccessToken() != null) {
            return credential;
        }

        return authorizeWithDialog(flow, clientSecrets);
    }

    /**
     * Авторизуется через диалоговые окна.
     *
     * @param flow поток авторизации
     * @param clientSecrets секреты клиента
     * @return удостоверение
     * @throws IOException если авторизация не успешна
     */
    private Credential authorizeWithDialog(GoogleAuthorizationCodeFlow flow,
                                           GoogleClientSecrets clientSecrets) throws IOException {
        String authorizationUrl = flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .build();

        showAuthorizationDialog(authorizationUrl);

        String authorizationCode = getAuthorizationCodeFromDialog();

        if (authorizationCode == null || authorizationCode.isEmpty()) {
            throw new IOException("Авторизация отменена пользователем");
        }

        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                flow.getTransport(),
                flow.getJsonFactory(),
                clientSecrets.getDetails().getClientId(),
                clientSecrets.getDetails().getClientSecret(),
                authorizationCode,
                REDIRECT_URI)
                .execute();

        return flow.createAndStoreCredential(tokenResponse, "user");
    }

    private void showAuthorizationDialog(String authorizationUrl) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Авторизация Google Calendar");
        alert.setHeaderText("Для работы с Google Calendar необходимо авторизоваться");
        alert.initStyle(StageStyle.UTILITY);
        alert.initModality(Modality.APPLICATION_MODAL);

        TextArea textArea = new TextArea("1. Скопируйте ссылку ниже:\n" +
                authorizationUrl + "\n\n" +
                "2. Вставьте её в браузер и авторизуйтесь\n" +
                "3. Скопируйте полученный код авторизации\n" +
                "4. Вставьте код в следующем окне");
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefSize(600, 300);

        ButtonType copyButton = new ButtonType("Копировать ссылку", ButtonBar.ButtonData.OTHER);
        ButtonType continueButton = new ButtonType("Продолжить", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(copyButton, continueButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == copyButton) {
                javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                content.putString(authorizationUrl);
                clipboard.setContent(content);

                Alert copiedAlert = new Alert(AlertType.INFORMATION);
                copiedAlert.setTitle("Скопировано");
                copiedAlert.setHeaderText("Ссылка скопирована в буфер обмена");
                copiedAlert.setContentText("Вставьте её в браузер для авторизации");
                copiedAlert.showAndWait();
            }
        });
    }

    /**
     * Попросит код авторизации аз диалога.
     *
     * @return код авторизации или пост строка
     */
    private String getAuthorizationCodeFromDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Код авторизации");
        dialog.setHeaderText("Введите код авторизации из браузера");
        dialog.setContentText("Код:");
        dialog.initStyle(StageStyle.UTILITY);
        dialog.initModality(Modality.APPLICATION_MODAL);

        Optional<String> result = dialog.showAndWait();
        return result.orElse("");
    }

    /**
     * Получает InputStream для ресурса.
     *
     * @param path путь к ресурсу
     * @return InputStream или null
     */
    private InputStream getResourceAsStream(String path) {
        InputStream stream = getClass().getResourceAsStream(path);

        if (stream == null) {
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);

            if (stream == null && path.startsWith("/")) {
                String pathWithoutSlash = path.substring(1);
                stream = getClass().getClassLoader().getResourceAsStream(pathWithoutSlash);
            }
        }

        return stream;
    }

    /**
     * Создает событие в Google Calendar.
     *
     * @param task задача для сохранения
     * @return ID созданного события
     * @throws IOException если ошибка на Google API
     */
    public String createTaskEvent(Task task) throws IOException {
        Event event = new Event()
                .setSummary(createEventSummary(task))
                .setDescription(createEventDescription(task))
                .setLocation("Учебная задача - CourseTP")
                .setColorId(getColorIdByStatus(task.getStatus().name()));

        if (task.getStartWork() != null && task.getEndWork() != null) {
            event.setStart(createEventDateTime(task.getStartWork()));
            event.setEnd(createEventDateTime(task.getEndWork()));
        } else if (task.getDeadline() != null) {
            LocalDateTime deadline = task.getDeadline();
            event.setStart(createEventDateTime(deadline));
            event.setEnd(createEventDateTime(deadline.plusHours(1)));
        } else {
            LocalDateTime now = LocalDateTime.now();
            event.setStart(createEventDateTime(now));
            event.setEnd(createEventDateTime(now.plusHours(1)));
        }

        try {
            Event createdEvent = calendarService.events()
                    .insert(CALENDAR_ID, event)
                    .execute();

            showSuccessDialog(createdEvent);
            return createdEvent.getId();

        } catch (IOException e) {
            showErrorDialog("Ошибка при создании события", e.getMessage());
            throw e;
        }
    }

    /**
     * Отображает диалог успеха.
     *
     * @param createdEvent созданное событие
     */
    private void showSuccessDialog(Event createdEvent) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Событие создано");
        alert.setHeaderText("Событие успешно добавлено в Google Calendar");

        StringBuilder content = new StringBuilder();
        content.append("ID события: ").append(createdEvent.getId()).append("\n");
        content.append("Заголовок: ").append(createdEvent.getSummary()).append("\n");

        if (createdEvent.getStart() != null && createdEvent.getStart().getDateTime() != null) {
            content.append("Начало: ").append(createdEvent.getStart().getDateTime()).append("\n");
        }

        if (createdEvent.getHtmlLink() != null) {
            content.append("Ссылка: ").append(createdEvent.getHtmlLink());
        }

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    /**
     * Отображает диалог ошибки.
     *
     * @param title заголовок
     * @param message сообщение
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Обрабатывает ошибки Google Calendar апи.
     *
     * @param e исключение ошибки
     * @throws IOException выбрасывает исключение
     */
    private void handleGoogleCalendarError(IOException e) throws IOException {
        String message = e.getMessage();

        if (message.contains("404")) {
            throw new IOException("Календарь не найден! Проверьте calendar_id: " + CALENDAR_ID, e);
        }
        if (message.contains("401") || message.contains("403")) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Ошибка авторизации");
            alert.setHeaderText("Необходима повторная авторизация");
            alert.setContentText("1. Удалите папку 'tokens/' в корне проекта\n" +
                    "2. Перезапустите приложение\n" +
                    "3. Снова авторизуйтесь");
            alert.showAndWait();
            throw new IOException("Ошибка авторизации. Удалите папку 'tokens' и перезапустите приложение", e);
        }
        if (message.contains("400")) {
            throw new IOException("Неверный запрос к Google Calendar. Проверьте параметры события", e);
        }

        throw new IOException("Ошибка Google Calendar: " + message, e);
    }

    /**
     * Составляет заголовок события.
     *
     * @param task задача
     * @return заголовок
     */
    private String createEventSummary(Task task) {
        return String.format("[%s] %s",
                task.getStatus().name().replace("_", " "),
                task.getTitle()
        );
    }

    /**
     * Составляет описание события.
     *
     * @param task задача
     * @return описание
     */
    private String createEventDescription(Task task) {
        StringBuilder description = new StringBuilder();
        description.append("Описание: ").append(task.getDescription()).append("\n\n");
        description.append("Статус: ").append(task.getStatus()).append("\n");
        description.append("Дедлайн: ").append(formatDateTime(task.getDeadline())).append("\n");

        if (task.getStartWork() != null) {
            description.append("Начало работы: ").append(formatDateTime(task.getStartWork())).append("\n");
        }
        if (task.getEndWork() != null) {
            description.append("Окончание работы: ").append(formatDateTime(task.getEndWork())).append("\n");
        }

        description.append("\nID задачи: ").append(task.getId());
        description.append("\nСтудент ID: ").append(task.getStudentId());
        description.append("\nКуратор ID: ").append(task.getCuratorId());
        description.append("\n\nСоздано через: ").append(APPLICATION_NAME);

        return description.toString();
    }

    private EventDateTime createEventDateTime(LocalDateTime dateTime) {
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        return new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(date))
                .setTimeZone(ZoneId.systemDefault().getId());
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Не указано";
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    private String getColorIdByStatus(String status) {
        switch (status.toUpperCase()) {
            case "TO_DO":
                return "5";
            case "IN_PROGRESS":
                return "2";
            case "DONE":
                return "10";
            case "OVERDUE":
                return "11";
            default:
                return "1";
        }
    }

    public boolean testConnection() throws IOException {
        try {
            calendarService.events()
                    .list(CALENDAR_ID)
                    .setMaxResults(1)
                    .execute();
            return true;
        } catch (Exception e) {
            handleGoogleCalendarError(new IOException(e));
            return false;
        }
    }

    /**
     * Отображает диалог с информацией.
     */
    public void showInfoDialog() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Информация о Google Calendar");
        alert.setHeaderText("Подключение к Google Calendar");
        alert.setContentText("Приложение подключено к Google Calendar\n" +
                "Календарь: " + CALENDAR_ID + "\n" +
                "Для управления календарем используйте веб-интерфейс Google Calendar");
        alert.showAndWait();
    }

    /**
     * Авторизуется вручную для Google Calendar.
     *
     * @return удостоверение
     * @throws IOException если ошибка
     * @throws GeneralSecurityException если проблема безопасности
     */
    public static Credential authorizeManually() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Файл secret.json не найден!");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        String url = flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI)
                .build();

        Alert urlAlert = new Alert(AlertType.INFORMATION);
        urlAlert.setTitle("Авторизация Google Calendar");
        urlAlert.setHeaderText("Перейдите по ссылке для авторизации");
        urlAlert.setContentText(url);
        urlAlert.showAndWait();

        TextInputDialog codeDialog = new TextInputDialog();
        codeDialog.setTitle("Код авторизации");
        codeDialog.setHeaderText("Введите код авторизации из браузера");
        codeDialog.setContentText("Код:");

        Optional<String> result = codeDialog.showAndWait();
        if (!result.isPresent() || result.get().isEmpty()) {
            throw new IOException("Авторизация отменена");
        }

        String code = result.get().trim();

        GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                clientSecrets.getDetails().getClientId(),
                clientSecrets.getDetails().getClientSecret(),
                code,
                REDIRECT_URI)
                .execute();

        return flow.createAndStoreCredential(response, "user");
    }
}