package controller;

import com.example.app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class InfosUpdatesController {

    @FXML private TextField emailField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextArea messageArea;

    // --- Navigation Bar handlers ---
    @FXML
    protected void goHome() throws Exception {
        MainApp.changeScene("/fxml/home.fxml");  // Changed from "/com/example/bookrides/home.fxml"
    }



    @FXML
    protected void goBookingHistory() throws Exception {
        MainApp.changeScene("/fxml/booking_history.fxml");  // Change similarly
    }

    @FXML
    protected void goFAQ() throws Exception {
        MainApp.changeScene("/fxml/faq.fxml");
    }

    @FXML
    protected void goOurRides() throws Exception {
        MainApp.changeScene("/fxml/our_rides.fxml");
    }

    @FXML
    protected void goInfosUpdates() throws Exception {
        // already here; do nothing or refresh
    }

    @FXML
    protected void goBookRides() throws Exception {
        MainApp.changeScene("/fxml/book_rides.fxml");
    }
    @FXML
    protected void onChatWithUsClick() throws Exception {
        MainApp.changeScene("/fxml/chatbot.fxml");
    }

    // --- Form submission ---
    @FXML
    protected void onSubmit() {
        String email = emailField.getText().trim();
        String first = firstNameField.getText().trim();
        String last  = lastNameField.getText().trim();
        String msg   = messageArea.getText().trim();

        if (email.isEmpty() || first.isEmpty() || msg.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in your email, first name and a message.");
            return;
        }

        // TODO: actually send/store the message here...

        showAlert(Alert.AlertType.INFORMATION, "Thank you, " + first + "! Your message has been submitted.");

        // clear fields
        emailField.clear();
        firstNameField.clear();
        lastNameField.clear();
        messageArea.clear();
    }

    private void showAlert(Alert.AlertType type, String content) {
        Alert a = new Alert(type);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}