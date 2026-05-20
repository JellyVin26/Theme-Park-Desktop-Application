package controller;

import com.example.app.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.User;

public class AdminHomeController {

    // Injected from your FXML for the welcome banner
    @FXML private Label welcomeLabel;

    // Injected from your FXML for the navigation buttons
    @FXML private Button homeButton;
    @FXML private Button faqButton;
    @FXML private Button viewBookingHistoriesButton;
    @FXML private Button makeBookingsButton;

    private User currentAdmin;

    /**
     * Called by LoginController.openAdminHome(...) to pass in the logged-in admin.
     */
    public void setUser(User user) {
        this.currentAdmin = user;
        welcomeLabel.setText("Welcome, Admin " + user.getFullName());
    }

    /** Handler for the HOME button */
    @FXML
    private void handleHome(ActionEvent event) {
        try {
            MainApp.changeScene("/fxml/AdminHome.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally show an alert here
        }
    }

    /** Handler for the FAQ button */
    @FXML
    private void handleFaq(ActionEvent event) {
        try {
            MainApp.changeScene("/fxml/Adminfaq.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally show an alert here
        }
    }

    /** Handler for the View Booking Histories button */
    @FXML
    private void handleViewBookingHistories(ActionEvent event) {
        try {
            MainApp.changeScene("/fxml/booking_history.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally show an alert here
        }
    }

    /** Handler for the Make Bookings button (user) */
    @FXML
    private void handleMakeBookings(ActionEvent event) {
        try {
            MainApp.changeScene("/fxml/book_rides.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally show an alert here
        }
    }

    /** Handler for the Admin Book Rides button */
    @FXML
    private void handleAdminBookRides(ActionEvent event) {
        try {
            MainApp.changeScene("/fxml/AdminBookRides.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally show an alert here
        }
    }



}
