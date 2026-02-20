package com.course.loadmonitorstudents.controller;

import com.course.loadmonitorstudents.dao.UserDAO;
import com.course.loadmonitorstudents.dao.db.UserDAOPSql;
import com.course.loadmonitorstudents.model.Role;
import com.course.loadmonitorstudents.model.User;
import com.course.loadmonitorstudents.util.PasswordUtil;
import com.course.loadmonitorstudents.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static com.course.loadmonitorstudents.util.Checker.*;

public class RegistrationController {

    private final UserDAO userDao = new UserDAOPSql();

    @FXML
    private TextField mailInput;

    @FXML
    private TextField nameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private TextField surnameInput;

    /**
     * Обрабатывает кнопку регистрации.
     * Проверяет данные и сохраняет нового пользователя.
     *
     * @throws Exception если ошибка базы данных
     */
    @FXML
    private void onRegButtonClick() throws Exception {
        String name = nameInput.getText().trim();
        String surname = surnameInput.getText().trim();
        String mail = mailInput.getText().trim();
        String password = passwordInput.getText().trim();

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

        if (userDao.findIdByEmailAndPassword(mail, password) != null) {
            showAlert("Ошибка", "Пользователь с таким email уже существует", Alert.AlertType.ERROR);
            return;
        }

        String hashPassword = PasswordUtil.hashPassword(password);
        User user  = new User(-1L, mail, hashPassword, name, surname, Role.CURATOR, null, null, null);
        userDao.create(user);

        showAlert("Успех", "Регистрация прошла успешно!\nТеперь вы можете войти в систему.", Alert.AlertType.INFORMATION);
        SceneManager.switchToAuthScene();
    }

    private void clearFields() {
        nameInput.clear();
        surnameInput.clear();
        mailInput.clear();
        passwordInput.clear();
    }

    /**
     * Обрабатывает кнопку возврата к авторизации.
     */
    @FXML
    private void onBackButtonClick() {
        clearFields();
        SceneManager.switchToAuthScene();
    }

    /**
     * Отображает диалог.
     *
     * @param title заголовок
     * @param message текст
     * @param type тип
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}