package com.course.loadmonitorstudents.controller;

import com.course.loadmonitorstudents.config.ApplicationConfig;
import com.course.loadmonitorstudents.dao.UserDAO;
import com.course.loadmonitorstudents.dao.db.UserDAOPSql;
import com.course.loadmonitorstudents.model.User;
import com.course.loadmonitorstudents.util.SceneManager;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

public class AuthorizationController {

    private UserDAO userDao = new UserDAOPSql();

    @FXML
    private Button authButton;

    @FXML
    private TextField mailInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Button regButton;

    @FXML
    private void onAuthButtonClick() throws Exception {
        String email = mailInput.getText().trim();
        String password = passwordInput.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Заполните почту и пароль", Alert.AlertType.WARNING);
            return;
        }

        User user = userDao.findIdByEmailAndPassword(email, password);
        if (user == null) {
            showAlert("Ошибка", "Неверный email или пароль", Alert.AlertType.ERROR);
            return;
        }

        mailInput.clear();
        passwordInput.clear();
        ApplicationConfig.setCurrentUser(user);
        if (user.getRole().equals("CURATOR"))
            SceneManager.switchToCuratorDashboard();
        else
            SceneManager.switchToStudentDashboard();
    }

    @FXML
    private void onRegButtonClick() {
        mailInput.clear();
        passwordInput.clear();
        SceneManager.switchToRegScene();
        System.out.println("Кнопка Регистрация нажата!");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
