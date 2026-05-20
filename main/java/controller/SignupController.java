package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import dao.UserDAO;

public class SignupController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleSignup() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phoneNumber = phoneField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        User newUser = new User(firstName, lastName, email, password, phoneNumber, "user");
        UserDAO.addUser(newUser);
        showAlert("Account created! Please log in.");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    @FXML
    private void openLogin() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene loginScene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/login.fxml")));
            stage.setScene(loginScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
