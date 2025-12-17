package com.course.loadmonitorstudents.controller;

import com.course.loadmonitorstudents.config.ApplicationConfig;
import com.course.loadmonitorstudents.dao.RestDAO;
import com.course.loadmonitorstudents.dao.TaskDAO;
import com.course.loadmonitorstudents.dao.db.RestDAOPSql;
import com.course.loadmonitorstudents.dao.db.TaskDAOPSql;
import com.course.loadmonitorstudents.dto.TaskWithCuratorDTO;
import com.course.loadmonitorstudents.model.Rest;
import com.course.loadmonitorstudents.model.StatusTask;
import com.course.loadmonitorstudents.model.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.course.loadmonitorstudents.util.Checker.isId;

public class StudentController {

    private TaskDAO taskDAO = new TaskDAOPSql();
    private RestDAO restDAO = new RestDAOPSql();

    private ObservableList<TaskWithCuratorDTO> taskData = FXCollections.observableArrayList();
    private ObservableList<Rest> restData = FXCollections.observableArrayList();

    @FXML
    private Button addButtons;

    @FXML
    private Button addButtont;

    @FXML
    private Button addButtontr;

    @FXML
    private Button calendarButtont;

    @FXML
    private Button calendarInputt;

    @FXML
    private TableColumn<TaskWithCuratorDTO, String> curatorSNColumn;

    @FXML
    private TableColumn<Rest, String> dayColumn;

    @FXML
    private TextField dayInput;

    @FXML
    private TableColumn<TaskWithCuratorDTO, String> deadlineColumn;

    @FXML
    private Button deleteButtons;

    @FXML
    private Button deleteButtontr;

    @FXML
    private Button deleteButtontt;

    @FXML
    private CheckBox descInputr;

    @FXML
    private CheckBox descInputt;

    @FXML
    private TableColumn<TaskWithCuratorDTO, String> descriptionColumn;

    @FXML
    private TableColumn<TaskWithCuratorDTO, String> endColumn;

    @FXML
    private TextField endInput;

    @FXML
    private Button filterButtonr;

    @FXML
    private Button filterButtont;

    @FXML
    private AnchorPane filterCombo;

    @FXML
    private ComboBox<String> filtrInputr;

    @FXML
    private ComboBox<String> filtrInputt;

    @FXML
    private TableColumn<Rest, String> hourColumn;

    @FXML
    private TextField hourInput;

    @FXML
    private TableColumn<Rest, Long> idColumnr;

    @FXML
    private TableColumn<TaskWithCuratorDTO, Long> idColumnt;

    @FXML
    private TableColumn<TaskWithCuratorDTO, Long> idCuratorColumn;

    @FXML
    private TextField idInputr;

    @FXML
    private TextField idInputt;

    @FXML
    private TextField passwordInput;

    @FXML
    private TextField passwordInput1;

    @FXML
    private TextField passwordInput2;

    @FXML
    private TableView<Rest> resetTable;

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
    private Button updateButtons;

    @FXML
    private Button updateButtont;

    @FXML
    private Button updateButtontr;

    @FXML
    public void initialize() {
        setupTaskTableColumns();
        loadTasksToTable();
        taskTable.setItems(taskData);
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
        idColumnr.setCellValueFactory(new PropertyValueFactory<>("id"));

        dayColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate();
            return new SimpleStringProperty(formatDate(date));
        });

        // Для часов используем свойство, которое будет рассчитываться на основе даты
        hourColumn.setCellValueFactory(cellData -> {
            // Здесь можно рассчитать количество часов отдыха
            // Например, если день выходной - 24 часа, если рабочий - 0 часов
            // Или можно хранить это значение в базе данных
            return new SimpleStringProperty("24"); // По умолчанию
        });
    }

    private void loadTasksToTable() {
        try {
            Long currentStudentId = ApplicationConfig.getCurrentUserId();
            List<TaskWithCuratorDTO> tasks = taskDAO.findAllTasksByStudentId(currentStudentId);

            updateTaskTable(tasks);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить задачи: " + e.getMessage(),
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
                    String curatorName = showInputDialog("Фильтр по куратору",
                            "Введите фамилию или имя куратора:", "");
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
            e.printStackTrace();
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

        if (idStr.isEmpty() || start.isEmpty() || end.isEmpty()) {
            showAlert("Ошибка", "Заполните все поля", Alert.AlertType.ERROR);
            return;
        }

        Long idTask;
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
        LocalDateTime startTime;
        LocalDateTime endTime;

        try {
            startTime = LocalDateTime.parse(start, formatter);
            endTime = LocalDateTime.parse(end, formatter);
        } catch (Exception e) {
            showAlert("Ошибка", "Неверный формат даты. Используйте: дд.ММ.гггг чч:мм:сс",
                    Alert.AlertType.ERROR);
            return;
        }

        if (endTime.isBefore(startTime)) {
            showAlert("Ошибка", "Время окончания не может быть раньше времени начала",
                    Alert.AlertType.ERROR);
            return;
        }

        LocalDateTime deadline = task.getDeadline();

        task.setStartWork(startTime);
        task.setStatus(StatusTask.IN_PROGRESS);

        if (deadline != null && deadline.isBefore(startTime)) {
            task.setStatus(StatusTask.OVERDUE);
        }

        task.setEndWork(endTime);

        if (endTime != null) {
            if (deadline != null && deadline.isBefore(endTime)) {
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

    private String showInputDialog(String title, String header, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
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