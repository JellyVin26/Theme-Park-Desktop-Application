package controller;

import com.example.app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


import java.util.List;
public class OurRidesController {
    @FXML
    private TextField emailField;
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
        //already here
    }

    @FXML
    protected void goInfosUpdates() throws Exception {
        MainApp.changeScene("/fxml/infos_updates.fxml");
    }

    @FXML
    protected void goBookRides() throws Exception {
        MainApp.changeScene("/fxml/book_rides.fxml");
    }
    @FXML
    protected void onChatWithUsClick() throws Exception {
        MainApp.changeScene("/fxml/chatbot.fxml");
    }

    @FXML protected void onBookSeatClick() throws Exception {
        MainApp.changeScene("/fxml/book_rides.fxml");
    }

}
