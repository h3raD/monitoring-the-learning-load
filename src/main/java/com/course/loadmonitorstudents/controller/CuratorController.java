package com.course.loadmonitorstudents.controller;

import com.course.loadmonitorstudents.config.ApplicationConfig;
import com.course.loadmonitorstudents.dao.TaskDAO;
import com.course.loadmonitorstudents.dao.UserDAO;
import com.course.loadmonitorstudents.dao.db.TaskDAOPSql;
import com.course.loadmonitorstudents.dao.db.UserDAOPSql;
import com.course.loadmonitorstudents.dto.TaskWithStudentDTO;
import com.course.loadmonitorstudents.model.Role;
import com.course.loadmonitorstudents.model.StatusTask;
import com.course.loadmonitorstudents.model.Task;
import com.course.loadmonitorstudents.model.User;
import com.course.loadmonitorstudents.service.PdfReportGenerator;
import com.course.loadmonitorstudents.service.TelegramNotificationService;
import com.course.loadmonitorstudents.util.PasswordUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.course.loadmonitorstudents.util.Checker.*;
import static com.course.loadmonitorstudents.util.Checker.noSpacesNoDigits;

public class CuratorController {

    private final UserDAO userDAO = new UserDAOPSql();
    private final TaskDAO taskDAO = new TaskDAOPSql();

    private final ObservableList<User> studentData = FXCollections.observableArrayList();

    private final ObservableList<TaskWithStudentDTO> taskData = FXCollections.observableArrayList();

    @FXML
    private TextField deadlineInput;

    @FXML
    private CheckBox descInput;

    @FXML
    private TextField descriptionInput;

    @FXML
    private ComboBox<String> filtrInput;

    @FXML
    private TextField idInputs;

    @FXML
    private TextField idInputt;

    @FXML
    private TextField mailInput;

    @FXML
    private TextField nameInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private ComboBox<String> statusInput;

    @FXML
    private TextField studentIdInput;

    @FXML
    private TextField surnameInput;

    @FXML
    private TableView<TaskWithStudentDTO> taskTable;

    @FXML
    private TableView<User> studentTable;

    @FXML
    private TextField titleInput;

    @FXML
    private TableColumn<User, Long> idColumn;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> surnameColumn;

    @FXML
    private TableColumn<User, String> mailColumn;

    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TableColumn<TaskWithStudentDTO, Long> taskIdColumn;

    @FXML
    private TableColumn<TaskWithStudentDTO, String> taskTitleColumn;

    @FXML
    private TableColumn<TaskWithStudentDTO, String> taskDescriptionColumn;

    @FXML
    private TableColumn<TaskWithStudentDTO, String> taskStatusColumn;

    @FXML
    private TableColumn<TaskWithStudentDTO, String> taskDeadlineColumn;

    @FXML
    private TableColumn<TaskWithStudentDTO, String> taskStartColumn;

    @FXML
    private TableColumn<TaskWithStudentDTO, String> taskEndColumn;

    @FXML
    private TableColumn<TaskWithStudentDTO, String> taskStudentLastNameColumn;

    @FXML
    private TableColumn<TaskWithStudentDTO, String> taskStudentFirstNameColumn;

    @FXML
    private TableColumn<TaskWithStudentDTO, Long> taskStudentIdColumn;

    @FXML
    public void initialize() {
        setupStudentTableColumns();
        setupTaskTableColumns();
        initializeFilterComboBox();
        loadTasksToTable();
        loadStudentsToTable();
    }

    private void setupStudentTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        mailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
    }

    private void setupTaskTableColumns() {
        taskIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        taskTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        taskDeadlineColumn.setCellValueFactory(cellData -> {
            LocalDateTime deadline = cellData.getValue().getDeadline();
            return new SimpleStringProperty(
                    deadline != null ? formatDateTime(deadline) : ""
            );
        });

        taskStartColumn.setCellValueFactory(cellData -> {
            LocalDateTime startWork = cellData.getValue().getStartWork();
            return new SimpleStringProperty(
                    startWork != null ? formatDateTime(startWork) : ""
            );
        });

        taskEndColumn.setCellValueFactory(cellData -> {
            LocalDateTime endWork = cellData.getValue().getEndWork();
            return new SimpleStringProperty(
                    endWork != null ? formatDateTime(endWork) : ""
            );
        });

        taskStudentLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentLastName"));
        taskStudentFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentFirstName"));
        taskStudentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null)
            return "";
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    @FXML
    private void onAddButtonsClick() throws Exception {
        String mail = mailInput.getText().trim();
        String password = passwordInput.getText().trim();
        String name = nameInput.getText().trim();
        String surname = surnameInput.getText().trim();

        if (name.isEmpty() || surname.isEmpty() || mail.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Заполните все поля", Alert.AlertType.WARNING);
            return;
        }

        if (!isValidEmail(mail)) {
            showAlert("Ошибка", "Неверный формат email", Alert.AlertType.ERROR);
            return;
        }

        if (!isAlphanumericNoSpaces(password)) {
            showAlert("Ошибка", "Пароль должен содержать только латинские буквы и цифры", Alert.AlertType.ERROR);
            return;
        }

        if (!noSpacesNoDigits(name)) {
            showAlert("Ошибка", "Имя не должно содержать цифры и пробелы", Alert.AlertType.ERROR);
            return;
        }

        if (!noSpacesNoDigits(surname)) {
            showAlert("Ошибка", "Фамилия не должна содержать цифры и пробелы", Alert.AlertType.ERROR);
            return;
        }

        if (userDAO.findIdByEmailAndPassword(mail, password) != null) {
            showAlert("Ошибка", "Пользователь с таким email уже существует", Alert.AlertType.ERROR);
            return;
        }

        String passwordHash = PasswordUtil.hashPassword(password);

        Long idCutator = ApplicationConfig.getCurrentUserId();
        User user  = new User(-1L, mail, passwordHash, name, surname, Role.STUDENT, idCutator, null, null);
        userDAO.create(user);

        loadStudentsToTable();
        clearInputFieldsStudents();
        showAlert("Успех", "Регистрация студента прошла успешно!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onUpdateButtonsClick() throws Exception {
        String id = idInputs.getText().trim();
        String mail = mailInput.getText().trim();
        String password = passwordInput.getText().trim();
        String name = nameInput.getText().trim();
        String surname = surnameInput.getText().trim();

        if (id.isEmpty() || (name.isEmpty() && surname.isEmpty() && mail.isEmpty() && password.isEmpty())) {
            showAlert("Ошибка", "Заполните id студента и хотя бы одно поле", Alert.AlertType.WARNING);
            return;
        }

        if (!isId(id)) {
            showAlert("Ошибка", "id - положительное числовое значение", Alert.AlertType.WARNING);
            return;
        }

        Long idCurator = ApplicationConfig.getCurrentUserId();
        User user = userDAO.findByIdAndCuratorId(Long.parseLong(id), idCurator);
        if (user == null) {
            showAlert("Ошибка", "Студент не найден", Alert.AlertType.WARNING);
            return;
        }

        if (!mail.isEmpty()) {
            user.setEmail(mail);
        }
        if (!password.isEmpty()) {
            String passwordHash = PasswordUtil.hashPassword(password);
            user.setPassword(passwordHash);
        }
        if (!name.isEmpty()) {
            user.setFirstName(name);
        }
        if (!surname.isEmpty()) {
            user.setLastName(surname);
        }

        userDAO.update(user);

        loadStudentsToTable();
        clearInputFieldsStudents();
        showAlert("Успех", "Студент обновлён", Alert.AlertType.WARNING);
    }

    @FXML
    private void onDeleteButtonsClick() throws Exception {
        String id = idInputs.getText().trim();
        if (id.isEmpty()) {
            showAlert("Ошибка", "Заполните id студента", Alert.AlertType.WARNING);
            return;
        }

        if (!isId(id)) {
            showAlert("Ошибка", "id - положительное числовое значение", Alert.AlertType.WARNING);
            return;
        }

        Long idCurator = ApplicationConfig.getCurrentUserId();
        User user = userDAO.findByIdAndCuratorId(Long.parseLong(id), idCurator);
        if (user == null) {
            showAlert("Ошибка", "Студент не найден", Alert.AlertType.WARNING);
            return;
        }

        userDAO.delete(Long.parseLong(id));

        loadStudentsToTable();
        clearInputFieldsStudents();
        showAlert("Успех", "Студент удалён", Alert.AlertType.WARNING);
    }

    @FXML
    private void onAddButtontClick() throws Exception {
        String title = titleInput.getText().trim();
        String description = descriptionInput.getText().trim();
        String deadline = deadlineInput.getText().trim();
        String idStudent = studentIdInput.getText().trim();

        if (title.isEmpty() && description.isEmpty()  && deadline.isEmpty() && idStudent.isEmpty()) {
            showAlert("Ошибка", "Заполните поля", Alert.AlertType.WARNING);
            return;
        }

        Long idCurator = ApplicationConfig.getCurrentUserId();
        User user = userDAO.findByIdAndCuratorId(Long.parseLong(idStudent), idCurator);
        if (user == null) {
            showAlert("Ошибка", "Студент не найден", Alert.AlertType.WARNING);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        if (!checkDate(deadline, formatter)) {
            showAlert("Ошибка", "Дата не соответствует формату: dd.MM.yyyy HH:mm:ss", Alert.AlertType.WARNING);
            return;
        }

        LocalDateTime deadlinedate = LocalDateTime.parse(deadline, formatter);
        LocalDateTime now = LocalDateTime.now();

        Task task = new Task(-1L, title, description, now, deadlinedate, null, null, StatusTask.TO_DO, user.getId(), idCurator);
        taskDAO.create(task);
        TelegramNotificationService.getInstance().notifyNewTask(task);

        loadTasksToTable();
        clearInputFieldsTasks();
        showAlert("Успех", "Задача добавлена", Alert.AlertType.WARNING);
    }

    @FXML
    private void onUpdateButtontClick() throws Exception {
        String id  = idInputt.getText().trim();
        String title = titleInput.getText().trim();
        String description = descriptionInput.getText().trim();
        String status = statusInput.getValue();
        String deadline = deadlineInput.getText().trim();
        String idStudent = studentIdInput.getText().trim();

        if (id.isEmpty() || (title.isEmpty() && description.isEmpty() && status.isEmpty() && deadline.isEmpty() && idStudent.isEmpty())) {
            showAlert("Ошибка", "Заполните поля", Alert.AlertType.WARNING);
            return;
        }

        if (!isId(idStudent)) {
            showAlert("Ошибка", "id студента - положительное числовое значение", Alert.AlertType.WARNING);
            return;
        }

        Long idCurator = ApplicationConfig.getCurrentUserId();
        User user = userDAO.findByIdAndCuratorId(Long.parseLong(idStudent), idCurator);
        if (user == null) {
            showAlert("Ошибка", "Студент не найден", Alert.AlertType.WARNING);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        if (!checkDate(deadline, formatter)) {
            showAlert("Ошибка", "Дата не соответствует формату: dd.MM.yyyy HH:mm:ss", Alert.AlertType.WARNING);
            return;
        }

        LocalDateTime deadlinedate = LocalDateTime.parse(deadline, formatter);
        LocalDateTime now = LocalDateTime.now();

        if (deadlinedate.isBefore(now)) {
            showAlert("Ошибка", "Дата и время дедлайна не может быть раньше даты и времени создания задачи", Alert.AlertType.WARNING);
            return;
        }

        Task task = new Task(-1L, title, description, now, deadlinedate, null, null, StatusTask.valueOf(status), user.getId(), idCurator);
        taskDAO.update(task);
        TelegramNotificationService.getInstance().sendNotificationToStudent(
                task.getStudentId(),
                "📝 *Задача обновлена*\n\n" +
                        "Название: " + task.getTitle() + "\n" +
                        "Новый дедлайн: " + formatDateTime(task.getDeadline()) + "\n" +
                        "Статус: " + task.getStatus()
        );


        loadTasksToTable();
        clearInputFieldsTasks();
        showAlert("Успех", "Задача добавлена", Alert.AlertType.WARNING);
    }

    @FXML
    private void onDeleteButtontClick() throws Exception {
        String id  = idInputt.getText().trim();

        if (id.isEmpty()) {
            showAlert("Ошибка", "Заполните id задачи", Alert.AlertType.WARNING);
            return;
        }

        if (!isId(id)) {
            showAlert("Ошибка", "id - положительное числовое значение", Alert.AlertType.WARNING);
            return;
        }

        Long idCurator = ApplicationConfig.getCurrentUserId();
        User user = userDAO.findByIdAndCuratorId(Long.parseLong(id), idCurator);
        if (user == null) {
            showAlert("Ошибка", "Студент не найден", Alert.AlertType.WARNING);
            return;
        }

        taskDAO.delete(Long.parseLong(id));

        loadTasksToTable();
        clearInputFieldsTasks();
        showAlert("Успех", "Студент удалён", Alert.AlertType.WARNING);
    }

    @FXML
    private void onTaskFilterButtonClick() throws Exception {
        try {
            String filterType = filtrInput.getValue();
            boolean descending = descInput.isSelected();

            if (filterType == null || filterType.isEmpty()) {
                showAlert("Ошибка", "Выберите тип фильтра", Alert.AlertType.WARNING);
                return;
            }

            Long currentCuratorId = ApplicationConfig.getCurrentUserId();
            List<TaskWithStudentDTO> tasks;

            switch (filterType) {
                case "Статус":
                    String status = getStatusForFilter();
                    if (status == null || status.isEmpty()) {
                        showAlert("Ошибка", "Введите статус для фильтрации",
                                Alert.AlertType.WARNING);
                        return;
                    }
                    tasks = taskDAO.findTasksByStatus(currentCuratorId, status, descending);
                    break;

                case "Дедлайн":
                    tasks = taskDAO.findTasksByDeadline(currentCuratorId, descending);
                    break;

                case "Фамилия и имя":
                    String nameFilter = nameInput.getText().trim();

                    if (nameFilter.isEmpty()) {
                        showAlert("Ошибка", "Введите фамилию или имя студента в поле 'Имя'",
                                Alert.AlertType.WARNING);
                        return;
                    }

                    tasks = taskDAO.findTasksByStudentName(currentCuratorId, nameFilter, descending);
                    break;

                case "ID":
                    String idStr = idInputt.getText().trim();
                    if (idStr.isEmpty()) {
                        showAlert("Ошибка", "Введите ID задачи",
                                Alert.AlertType.WARNING);
                        return;
                    }
                    if (!isId(idStr)) {
                        showAlert("Ошибка", "ID должен быть числом",
                                Alert.AlertType.WARNING);
                        return;
                    }
                    Long taskId = Long.parseLong(idStr);
                    tasks = taskDAO.findTasksById(currentCuratorId, taskId, descending);
                    break;

                default:
                    tasks = taskDAO.findAllTasksWithStudentByCuratorId(currentCuratorId);
                    break;
            }

            updateTaskTable(tasks);

            showAlert("Успех", "Найдено задач: " + tasks.size(),
                    Alert.AlertType.INFORMATION);

        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка фильтрации: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onGeneratePdfButtonClick() throws Exception {
        try {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Текущие задачи",
                    "Текущие задачи", "Пользователи (без паролей)");
            dialog.setTitle("Тип отчета");
            dialog.setHeaderText("Выберите тип отчета для генерации");
            dialog.setContentText("Тип:");

            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty())
                return;

            String reportType = result.get();

            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Сохранить отчет PDF");
            fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            String defaultFileName = reportType.toLowerCase()
                    .replace(" ", "_") + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")) + ".pdf";
            fileChooser.setInitialFileName(defaultFileName);

            File file = fileChooser.showSaveDialog(null);
            if (file == null) {
                return;
            }

            String filePath = file.getAbsolutePath();

            if ("Пользователи (без паролей)".equals(reportType)) {
                Long currentCuratorId = ApplicationConfig.getCurrentUserId();
                List<User> users = userDAO.findAllStudentsByCuratorId(currentCuratorId);

                for (User user : users) {
                    user.setPassword("********");
                }

                PdfReportGenerator.generateUserReport(users, filePath);
                showAlert("Успех", "Отчет по пользователям сохранен",
                        Alert.AlertType.INFORMATION);

            } else {
                List<TaskWithStudentDTO> tasks;

                if (taskData.isEmpty()) {
                    Long currentCuratorId = ApplicationConfig.getCurrentUserId();
                    tasks = taskDAO.findAllTasksWithStudentByCuratorId(currentCuratorId);
                } else {
                    tasks = taskData;
                }

                String filterType = filtrInput.getValue();
                boolean descending = descInput.isSelected();
                PdfReportGenerator.generateTaskReport(tasks, filterType, descending, filePath);
                showAlert("Успех", "Отчет по задачам сохранен",
                        Alert.AlertType.INFORMATION);
            }

        } catch (Exception e) {
            showAlert("Ошибка", "Ошибка генерации отчета: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadStudentsToTable() {
        try {
            Long currentCuratorId = ApplicationConfig.getCurrentUserId();
            List<User> students = userDAO.findAllStudentsByCuratorId(currentCuratorId);

            studentData.clear();
            studentData.addAll(students);
            studentTable.setItems(studentData);

        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить список студентов: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTasksToTable() {
        try {
            Long currentCuratorId = ApplicationConfig.getCurrentUserId();
            List<TaskWithStudentDTO> tasks = taskDAO.findAllTasksWithStudentByCuratorId(currentCuratorId);
            updateTaskTable(tasks);
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить список задач: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void clearInputFieldsStudents() {
        idInputs.clear();
        mailInput.clear();
        passwordInput.clear();
        nameInput.clear();
        surnameInput.clear();
    }

    private void clearInputFieldsTasks() {
        idInputt.clear();
        statusInput.setValue(null);
        titleInput.clear();
        descriptionInput.clear();
        deadlineInput.clear();
        studentIdInput.clear();
    }

    private String getStatusForFilter() {
        if (statusInput != null) {
            return statusInput.getValue();
        }
        return showStatusDialog();
    }

    private String showStatusDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Фильтр по статусу");
        dialog.setHeaderText("Введите статус задачи");
        dialog.setContentText("Статус (TO_DO, IN_PROGRESS, DONE и т.д.):");
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void updateTaskTable(List<TaskWithStudentDTO> tasks) {
        taskData.clear();
        if (tasks != null && !tasks.isEmpty())
            taskData.addAll(tasks);

        taskTable.setItems(taskData);
        taskTable.refresh();
    }

    private void initializeFilterComboBox() {
        if (statusInput != null) {
            statusInput.getItems().addAll(
                    "TO_DO",
                    "IN_PROGRESS",
                    "DONE",
                    "OVERDUE"
            );
        }
    }
}
