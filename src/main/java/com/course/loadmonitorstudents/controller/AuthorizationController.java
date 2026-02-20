package com.course.loadmonitorstudents.controller;

import com.course.loadmonitorstudents.config.ApplicationConfig;
import com.course.loadmonitorstudents.dao.UserDAO;
import com.course.loadmonitorstudents.dao.db.UserDAOPSql;
import com.course.loadmonitorstudents.model.Role;
import com.course.loadmonitorstudents.model.User;
import com.course.loadmonitorstudents.util.PasswordUtil;
import com.course.loadmonitorstudents.util.SceneManager;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

public class AuthorizationController {

    private final UserDAO userDao = new UserDAOPSql();

    @FXML
    private TextField mailInput;

    @FXML
    private PasswordField passwordInput;

    /**
     * Обрабатывает кнопку авторизации.
     * Проверяет email и пароль, авторизует пользователя.
     *
     * @throws Exception если ошибка базы данных
     */
    @FXML
    private void onAuthButtonClick() throws Exception {
        String email = mailInput.getText().trim();
        String password = passwordInput.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Заполните почту и пароль", Alert.AlertType.WARNING);
            return;
        }

        String hashPassword = PasswordUtil.hashPassword(password);

        User user = userDao.findByEmail(email);
        if (user == null) {
            showAlert("Неверный email или пароль", Alert.AlertType.ERROR);
            return;
        }

        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            showAlert("Неверный email или пароль", Alert.AlertType.ERROR);
            return;
        }

        mailInput.clear();
        passwordInput.clear();
        ApplicationConfig.setCurrentUser(user);
        if (user.getRole() == Role.CURATOR)
            SceneManager.switchToCuratorDashboard();
        else
            SceneManager.switchToStudentDashboard();
    }

    /**
     * Обрабатывает кнопку перехода на сцену регистрации.
     */
    @FXML
    private void onRegButtonClick() {
        mailInput.clear();
        passwordInput.clear();
        SceneManager.switchToRegScene();
        System.out.println("Кнопка Регистрация нажата!");
    }

    /**
     * Отображает диалог с сообщением.
     *
     * @param message текст сообщения
     * @param type тип алерта
     */
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
