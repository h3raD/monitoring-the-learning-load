package com.course.loadmonitorstudents.controller;

import com.course.loadmonitorstudents.config.AppLifecycleService;
import com.course.loadmonitorstudents.config.ApplicationConfig;
import com.course.loadmonitorstudents.dao.RestDAO;
import com.course.loadmonitorstudents.dao.TaskDAO;
import com.course.loadmonitorstudents.dao.UserDAO;
import com.course.loadmonitorstudents.dao.db.RestDAOPSql;
import com.course.loadmonitorstudents.dao.db.TaskDAOPSql;
import com.course.loadmonitorstudents.dao.db.UserDAOPSql;
import com.course.loadmonitorstudents.dto.TaskWithCuratorDTO;
import com.course.loadmonitorstudents.model.Rest;
import com.course.loadmonitorstudents.model.StatusTask;
import com.course.loadmonitorstudents.model.Task;
import com.course.loadmonitorstudents.model.User;
import com.course.loadmonitorstudents.service.GoogleCalendarService;
import com.course.loadmonitorstudents.service.TelegramNotificationService;
import com.course.loadmonitorstudents.util.PasswordUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static com.course.loadmonitorstudents.util.Checker.checkDate;
import static com.course.loadmonitorstudents.util.Checker.isId;

public class StudentController {

    private final UserDAO userDAO = new UserDAOPSql();
    private final TaskDAO taskDAO = new TaskDAOPSql();
    private final RestDAO restDAO = new RestDAOPSql();

    private final ObservableList<TaskWithCuratorDTO> taskData = FXCollections.observableArrayList();
    private final ObservableList<Rest> restData = FXCollections.observableArrayList();

    private GoogleCalendarService googleCalendarService;

    @FXML
    private TextField tgInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private TextField googleInput;

    @FXML
    private TableColumn<TaskWithCuratorDTO, String> curatorSNColumn;

    @FXML
    private TableColumn<Rest, String> dataColumn;

    @FXML
    private TableColumn<TaskWithCuratorDTO, String> deadlineColumn;

    @FXML
    private CheckBox descInputt;

    @FXML
    private TableColumn<TaskWithCuratorDTO, String> descriptionColumn;

    @FXML
    private TableColumn<TaskWithCuratorDTO, String> endColumn;

    @FXML
    private TextField endInput;

    @FXML
    private ComboBox<String> filtrInputt;

    @FXML
    private TableColumn<Rest, String> hoursColumn;

    @FXML
    private TextField hoursInput;

    @FXML
    private TextField dataInput;

    @FXML
    private TableColumn<Rest, Long> idColumnRest;

    @FXML
    private TableColumn<TaskWithCuratorDTO, Long> idColumnt;

    @FXML
    private TableColumn<TaskWithCuratorDTO, Long> idCuratorColumn;

    @FXML
    private TextField idInputt;

    @FXML
    private TableView<Rest> restTable;

    @FXML
    private TableColumn<TaskWithCuratorDTO, String> startColumn;

    @FXML
    private TextField startInput;

    @FXML
    private TableColumn<TaskWithCuratorDTO, String> statusColumn;

    @FXML
    private TableView<TaskWithCuratorDTO> taskTable;

    @FXML
    private TableColumn<TaskWithCuratorDTO, String> titleColumn;

    @FXML
    private TableView<User> settingsTable;

    @FXML
    private TableColumn<User, String> telegramColumn;

    @FXML
    private TableColumn<User, String> googleColumn;

    @FXML
    private TableColumn<User, String> passwordColumn;

    private final ObservableList<User> settingsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTaskTableColumns();
        setupRestTableColumns();
        setupSettingsTableColumns();
        loadTasksToTable();
        loadRestsToTable();
        loadSettingsToTable();
        taskTable.setItems(taskData);
        restTable.setItems(restData);
        if (settingsTable != null) {
            settingsTable.setItems(settingsData);
        }
        AppLifecycleService.initialize();
        if (filtrInputt != null) {
            filtrInputt.getSelectionModel().selectFirst();
        }
    }

    private void setupSettingsTableColumns() {
        telegramColumn.setCellValueFactory(cellData -> {
            Long telegramId = cellData.getValue().getTelegramID();
            return new SimpleStringProperty(telegramId != null ? telegramId.toString() : "Не привязан");
        });

        googleColumn.setCellValueFactory(cellData -> {
            String googleKey = cellData.getValue().getGoogleCalendarApiKey();
            return new SimpleStringProperty(googleKey != null && !googleKey.isEmpty() ? "Привязан" : "Не привязан");
        });

        passwordColumn.setCellValueFactory(cellData -> {
            String password = cellData.getValue().getPassword();
            return new SimpleStringProperty(password != null && !password.isEmpty() ? "********" : "Не установлен");
        });
    }

    private void loadSettingsToTable() {
        try {
            User currentUser = ApplicationConfig.getCurrentUser();
            if (currentUser != null) {
                settingsData.clear();
                settingsData.add(currentUser);
                settingsTable.setItems(settingsData);
                settingsTable.refresh();
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось загрузить настройки: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onCalendarInputtClick() {
        try {
            User currentUser = ApplicationConfig.getCurrentUser();
            if (currentUser == null) {
                showAlert("Ошибка", "Вы не авторизованы в системе", Alert.AlertType.ERROR);
                return;
            }

            try {
                java.net.URL resource = getClass().getResource("/com/course/loadmonitorstudents/google/secret.json");
                if (resource == null) {
                    showAlert("Ошибка конфигурации",
                            "Файл secret.json не найден!\n\n" +
                                    "Поместите файл в папку: src/main/resources/\n" +
                                    "Получите его из Google Cloud Console",
                            Alert.AlertType.ERROR);
                    return;
                }
            } catch (Exception e) {
                showAlert("Ошибка", "Не удалось проверить файл secret.json", Alert.AlertType.ERROR);
                return;
            }

            if (googleCalendarService == null) {
                try {
                    googleCalendarService = new GoogleCalendarService();
                    System.out.println("Google Calendar Service создан");
                } catch (Exception e) {
                    String errorMsg = e.getMessage();
                    String detailedMsg = "Ошибка создания Google Calendar Service:\n" + errorMsg;

                    if (errorMsg.contains("com/course/loadmonitorstudents/google/secret.json") || errorMsg.contains("client_id")) {
                        detailedMsg += "\n\nПроверьте файл secret.json в resources/";
                    } else if (errorMsg.contains("8888")) {
                        detailedMsg += "\n\nПорт 8888 занят. Измените порт в GoogleCalendarService.java";
                    } else if (errorMsg.contains("401") || errorMsg.contains("403")) {
                        detailedMsg += "\n\nУдалите папку 'tokens/' и попробуйте снова";
                    }

                    showAlert("Ошибка инициализации", detailedMsg, Alert.AlertType.ERROR);
                    return;
                }
            }

            Long studentId = ApplicationConfig.getCurrentUserId();
            List<TaskWithCuratorDTO> tasks;
            try {
                tasks = taskDAO.findAllTasksByStudentId(studentId);
            } catch (SQLException e) {
                showAlert("Ошибка БД", "Не удалось получить задачи: " + e.getMessage(),
                        Alert.AlertType.ERROR);
                return;
            }

            if (tasks.isEmpty()) {
                showAlert("Информация", "У вас нет задач для добавления",
                        Alert.AlertType.INFORMATION);
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Добавление в Google Calendar");
            confirmation.setHeaderText("Добавить " + tasks.size() + " задач в календарь?");
            confirmation.setContentText(
                    "Первая синхронизация откроет браузер\n" +
                            "для авторизации в Google.\n\n" +
                            "Календарь: 'Курсовая'"
            );

            if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.CANCEL) {
                return;
            }

            int added = 0;
            int errors = 0;

            for (TaskWithCuratorDTO taskDTO : tasks) {
                try {
                    Task task = taskDAO.getById(taskDTO.getId());
                    if (task != null) {
                        String eventId = googleCalendarService.createTaskEvent(task);
                        added++;
                        System.out.println("Добавлено: " + task.getTitle());
                    }
                } catch (Exception e) {
                    errors++;
                    System.err.println("Ошибка: " + taskDTO.getTitle() + " - " + e.getMessage());

                    if (e.getMessage().contains("401") || e.getMessage().contains("403")) {
                        showAlert("Ошибка авторизации",
                                "Требуется повторная авторизация.\n" +
                                        "Удалите папку 'tokens/' и попробуйте снова.",
                                Alert.AlertType.ERROR);
                        return;
                    }
                }
            }

            String resultMsg = " Добавлено задач: " + added + " из " + tasks.size();
            if (errors > 0) {
                resultMsg += "\n  Ошибок: " + errors;
            }
            resultMsg += "\n\nОткройте Google Calendar для просмотра";

            showAlert("Синхронизация завершена", resultMsg, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            showAlert("Критическая ошибка",
                    "Непредвиденная ошибка: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void setupTaskTableColumns() {
        idColumnt.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        deadlineColumn.setCellValueFactory(cellData -> {
            LocalDateTime deadline = cellData.getValue().getDeadline();
            return new SimpleStringProperty(formatDateTime(deadline));
        });

        startColumn.setCellValueFactory(cellData -> {
            LocalDateTime start = cellData.getValue().getStartWork();
            return new SimpleStringProperty(formatDateTime(start));
        });

        endColumn.setCellValueFactory(cellData -> {
            LocalDateTime end = cellData.getValue().getEndWork();
            return new SimpleStringProperty(formatDateTime(end));
        });

        idCuratorColumn.setCellValueFactory(new PropertyValueFactory<>("curatorId"));

        curatorSNColumn.setCellValueFactory(cellData -> {
            TaskWithCuratorDTO task = cellData.getValue();
            return new SimpleStringProperty(task.getCuratorFullName());
        });
    }

    private void setupRestTableColumns() {
        idColumnRest.setCellValueFactory(new PropertyValueFactory<>("id"));

        dataColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate();
            return new SimpleStringProperty(formatDate(date));
        });

        hoursColumn.setCellValueFactory(cellData -> {
            Integer hours = cellData.getValue().getHours();
            return new SimpleStringProperty(hours != null ? hours.toString() : "0");
        });
    }

    private void loadTasksToTable() {
        try {
            Long currentStudentId = ApplicationConfig.getCurrentUserId();
            List<TaskWithCuratorDTO> tasks = taskDAO.findAllTasksByStudentId(currentStudentId);

            updateTaskTable(tasks);

        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось загрузить задачи: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void updateRestTable(List<Rest> rests) {
        restData.clear();
        if (rests != null && !rests.isEmpty()) {
            restData.addAll(rests);
        }
        restTable.setItems(restData);
        restTable.refresh();
    }

    private void loadRestsToTable() {
        try {
            Long currentStudentId = ApplicationConfig.getCurrentUserId();
            List<Rest> rests = restDAO.findByStudentId(currentStudentId);
            updateRestTable(rests);
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось загрузить данные об отдыхе: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void updateTaskTable(List<TaskWithCuratorDTO> tasks) {
        taskData.clear();

        if (tasks != null && !tasks.isEmpty()) {
            taskData.addAll(tasks);
        }

        taskTable.setItems(taskData);
        taskTable.refresh();
    }

    private String formatDate(LocalDate date) {
        if (date == null)
            return "";
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null)
            return "";
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    @FXML
    private void onFilterButtontClick() {
        try {
            String filterType = filtrInputt.getValue();
            boolean descending = descInputt.isSelected();

            if (filterType == null || filterType.isEmpty()) {
                showAlert("Ошибка", "Выберите тип фильтра", Alert.AlertType.WARNING);
                return;
            }

            Long currentStudentId = ApplicationConfig.getCurrentUserId();
            List<TaskWithCuratorDTO> tasks;

            switch (filterType) {
                case "Статус":
                    String status = showStatusDialog();
                    if (status == null || status.isEmpty()) {
                        return;
                    }
                    tasks = taskDAO.findTasksByStatusForStudent(currentStudentId, status, descending);
                    break;

                case "Дедлайн":
                    tasks = taskDAO.findTasksByDeadlineForStudent(currentStudentId, descending);
                    break;

                case "Фамилия и имя":
                    String curatorName = showInputDialog(
                    );
                    if (curatorName == null || curatorName.isEmpty()) {
                        return;
                    }
                    tasks = taskDAO.findTasksByCuratorNameForStudent(currentStudentId, curatorName, descending);
                    break;

                case "ID":
                    String idStr = idInputt.getText().trim();
                    if (idStr.isEmpty()) {
                        showAlert("Ошибка", "Введите ID задачи в поле 'ID задачи'",
                                Alert.AlertType.WARNING);
                        return;
                    }
                    if (!isId(idStr)) {
                        showAlert("Ошибка", "ID должен быть числом",
                                Alert.AlertType.WARNING);
                        return;
                    }
                    Long taskId = Long.parseLong(idStr);
                    tasks = taskDAO.findTaskByIdForStudent(currentStudentId, taskId, descending);
                    break;

                default:
                    tasks = taskDAO.findAllTasksByStudentId(currentStudentId);
                    break;
            }

            updateTaskTable(tasks);

            if (tasks.isEmpty()) {
                showAlert("Результат", "Задачи не найдены", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Успех", "Найдено задач: " + tasks.size(),
                        Alert.AlertType.INFORMATION);
            }

        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка фильтрации: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onResetFilters() {
        if (filtrInputt != null) {
            filtrInputt.getSelectionModel().clearSelection();
        }
        if (descInputt != null) {
            descInputt.setSelected(false);
        }

        loadTasksToTable();

        showAlert("Информация", "Фильтры сброшены", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onAddButtontClick() throws Exception {
        String idStr = idInputt.getText().trim();
        String start = startInput.getText().trim();
        String end = endInput.getText().trim();
        LocalDateTime startdate = null;
        LocalDateTime enddate = null;

        if (idStr.isEmpty() && start.isEmpty() && end.isEmpty()) {
            showAlert("Ошибка", "Заполните поля", Alert.AlertType.ERROR);
            return;
        }

        if (idStr.isEmpty()) {
            showAlert("Ошибка", "Введите ID задачи", Alert.AlertType.ERROR);
            return;
        }

        if (start.isEmpty() && end.isEmpty()) {
            showAlert("Ошибка", "Заполните дату начала или окончания", Alert.AlertType.ERROR);
            return;
        }

        long idTask;
        try {
            idTask = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "ID задачи должен быть числом", Alert.AlertType.ERROR);
            return;
        }

        Long currentStudentId = ApplicationConfig.getCurrentUserId();

        Task task = taskDAO.getTaskByIdAndTaskId(idTask, currentStudentId);
        if (task == null) {
            showAlert("Ошибка", "Задача не найдена или не принадлежит вам", Alert.AlertType.ERROR);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        if (!start.isEmpty() && !checkDate(start, formatter)) {
            showAlert("Ошибка", "Дата старта не соответствует формату: dd.MM.yyyy HH:mm:ss", Alert.AlertType.WARNING);
            return;
        }
        if (!end.isEmpty() && !checkDate(end, formatter)) {
            showAlert("Ошибка", "Дата сдачи не соответствует формату: dd.MM.yyyy HH:mm:ss", Alert.AlertType.WARNING);
            return;
        }

        if (!start.isEmpty())
            startdate = LocalDateTime.parse(start, formatter);
        if (!end.isEmpty())
            enddate = LocalDateTime.parse(end, formatter);

        if (!end.isEmpty() && !start.isEmpty() && enddate.isBefore(startdate)) {
            showAlert("Ошибка", "Время окончания не может быть раньше времени начала",
                    Alert.AlertType.ERROR);
            return;
        }

        LocalDateTime deadline = task.getDeadline();

        if (startdate != null) {
            task.setStartWork(startdate);
            task.setStatus(StatusTask.IN_PROGRESS);


        if (deadline != null && deadline.isBefore(startdate)) {
            task.setStatus(StatusTask.OVERDUE);
        }
        }

        if (enddate != null) {
            task.setEndWork(enddate);

            if (deadline != null && deadline.isBefore(enddate)) {
                task.setStatus(StatusTask.OVERDUE);
            } else {
                task.setStatus(StatusTask.DONE);
            }
        }

        taskDAO.update(task);

        loadTasksToTable();

        clearInputFields();

        showAlert("Успех", "Время работы над задачей обновлено", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onAddButtontgpClick() throws Exception {
        String telegram = tgInput.getText().trim();
        String google = googleInput.getText().trim();

        if (telegram.isEmpty() && google.isEmpty()) {
            showAlert("Ошибка", "Заполните Telegram или Google Calendar",
                    Alert.AlertType.ERROR);
            return;
        }

        User user = ApplicationConfig.getCurrentUser();

        if (!telegram.isEmpty()) {
            long telegramIdnum;
            try {
                telegramIdnum = Long.parseLong(telegram.trim());
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "ID Telegram должен быть целым числом",
                        Alert.AlertType.ERROR);
                return;
            }
            user.setTelegramID(telegramIdnum);
        }
        if (!google.isEmpty()) {
            user.setGoogleCalendarApiKey(google);
        }

        userDAO.update(user);

        tgInput.clear();
        googleInput.clear();
        loadSettingsToTable();

        showAlert("Успех", "Настройки обновлены", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onUpdateButtontgpClick() throws Exception {
        String telegram = tgInput.getText().trim();
        String google = googleInput.getText().trim();
        String password = passwordInput.getText().trim();

        if (telegram.isEmpty() && google.isEmpty() && password.isEmpty()) {
            showAlert("Ошибка", "Ни одно поле не заполнено",
                    Alert.AlertType.ERROR);
            return;
        }

        User user = ApplicationConfig.getCurrentUser();

        if (!telegram.isEmpty()) {
            long telegramIdnum;
            try {
                telegramIdnum = Long.parseLong(telegram.trim());
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "ID Telegram должен быть целым числом",
                        Alert.AlertType.ERROR);
                return;
            }
            user.setTelegramID(telegramIdnum);
        }
        if (!google.isEmpty()) {
            user.setGoogleCalendarApiKey(google);
        }
        if (!password.isEmpty()) {
            if (password.length() < 6) {
                showAlert("Ошибка", "Пароль должен содержать минимум 6 символов",
                        Alert.AlertType.ERROR);
                return;
            }
            String hashPassword = PasswordUtil.hashPassword(password);
            user.setPassword(hashPassword);
        }

        userDAO.update(user);

        tgInput.clear();
        googleInput.clear();
        passwordInput.clear();
        loadSettingsToTable();

        showAlert("Успех", "Настройки обновлены", Alert.AlertType.INFORMATION);

    }

    @FXML
    private void onAddButtonRestClick() throws Exception {
        String data = dataInput.getText().trim();
        String hours = hoursInput.getText().trim();

        if (data.isEmpty() && hours.isEmpty()) {
            showAlert("Ошибка", "поля не заполнены",
                    Alert.AlertType.ERROR);
            return;
        }

        LocalDate lData;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try {
            lData = LocalDate.parse(data, formatter);
        } catch (DateTimeParseException e) {
            showAlert("Ошибка", "Неверный формат даты. Используйте: дд.ММ.гггг",
                    Alert.AlertType.ERROR);
            return;
        }

        int iHours;
        try {
            iHours = Integer.parseInt(hours.trim());
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите положительное целое число для количества часов сна",
                    Alert.AlertType.ERROR);
            return;
        }

        Rest rest = new Rest(-1L, lData, iHours, ApplicationConfig.getCurrentUserId());

        restDAO.create(rest);
        TelegramNotificationService.getInstance().notifySleepRecommendation(
                ApplicationConfig.getCurrentUserId(),
                iHours
        );
        showAlert("Успех", "", Alert.AlertType.INFORMATION);
    }

    private void clearInputFields() {
        idInputt.clear();
        startInput.clear();
        endInput.clear();
    }

    private String showStatusDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Фильтр по статусу");
        dialog.setHeaderText("Введите статус задачи");
        dialog.setContentText("Статус (TO_DO, IN_PROGRESS, DONE, OVERDUE):");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private String showInputDialog() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Фильтр по куратору");
        dialog.setHeaderText("Введите фамилию или имя куратора:");
        dialog.setContentText("Значение:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}