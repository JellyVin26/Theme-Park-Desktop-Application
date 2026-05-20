package controller;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import util.Session;

// Import your controllers from the same package


public class LoginController {
    @FXML private TextField    emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String email    = emailField.getText();
        String password = passwordField.getText();

        for (User user : UserDAO.getAllUsers()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                // Store in session
                Session.loggedInEmail = email;

                // Route based on role
                if ("admin".equalsIgnoreCase(user.getRole())) {
                    openAdminHome(user);
                } else {
                    openUserHome(user);
                }
                return;
            }
        }

        new Alert(Alert.AlertType.ERROR, "Invalid email or password!").showAndWait();
    }

    @FXML
    private void openSignup() throws Exception {
        Stage stage = (Stage) emailField.getScene().getWindow();
        Scene signupScene = new Scene(
                FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"))
        );
        stage.setScene(signupScene);
    }

    private void openUserHome(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/home.fxml")
            );
            Scene scene = new Scene(loader.load());

            HomeController ctrl = loader.getController();
            ctrl.setUser(user);  // <-- requires HomeController#setUser(User)

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openAdminHome(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/AdminHome.fxml")
            );
            Scene scene = new Scene(loader.load());

            AdminHomeController ctrl = loader.getController();
            ctrl.setUser(user);  // <-- requires AdminHomeController#setUser(User)

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

