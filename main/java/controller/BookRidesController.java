package controller;

import com.example.app.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static util.Session.loggedInEmail;

public class BookRidesController {

    @FXML private ComboBox<String> rideDropdown;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeDropdown;
    @FXML private TextField adultField, seniorField, studentField, familyField;

    private final String TICKET_FILE = "combined-javafx/tickets.txt";

    // --- Booking logic ---
    @FXML
    protected void onViewRideAvailabilityClick() {
        try {
            MainApp.changeScene("/fxml/ride_availability.fxml");
        } catch (Exception e) {
            showError("Error loading ride availability page: " + e.getMessage());
        }
    }

    @FXML
    protected void onBookClick() {
        try {
            // 1) Ensure all required fields are filled
            if (!validateForm()) return;

            // 2) Gather form data
            String ride     = rideDropdown.getValue();
            String date     = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String time     = timeDropdown.getValue();
            String adults   = getOrDefault(adultField.getText());
            String seniors  = getOrDefault(seniorField.getText());
            String students = getOrDefault(studentField.getText());
            String families = getOrDefault(familyField.getText());

            // 3) Generate ticket ID and status
            String ticketId = generateTicketId();
            String status   = generateRandomStatus();

            // 4) Build a summary for user feedback
            String summary = String.format(
                    "Ticket ID: %s\nEmail: %s\nRide: %s\nDate: %s\nTime: %s\nAdults: %s\nSenior/Oku: %s\nStudents: %s\nFamily Packages: %s\nStatus: %s",
                    ticketId, loggedInEmail, ride, date, time, adults, seniors, students, families, status
            );

            // 5) Persist to file
            saveTicketToFile(ticketId, ride, date, time, adults, seniors, students, families, status);

            // 6) Send confirmation email (if logged in)
            String userEmail = loggedInEmail;
            if (userEmail == null || userEmail.isBlank()) {
                // user isn’t logged in or email not set
                showInfo("Booking confirmed!\n\n" + summary +
                        "\n\n(Log in next time to receive a confirmation email.)");
            } else {
                boolean emailOk = AutoEmailSenderController.sendBookingEmail(
                        userEmail,
                        ticketId, ride, date, time,
                        adults, seniors, students, families, status
                );

                if (emailOk) {
                    showInfo("Booking confirmed and confirmation email sent to:\n" + userEmail + "\n\n" + summary);
                } else {
                    showError("Booking confirmed, but failed to send confirmation email to:\n" + userEmail +
                            "\n\n" + summary);
                }
            }

        } catch (Exception e) {
            showError("An error occurred during booking: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        if (rideDropdown.getValue() == null) {
            showError("Please select a ride."); return false;
        }
        if (datePicker.getValue() == null) {
            showError("Please select a date."); return false;
        }
        if (timeDropdown.getValue() == null) {
            showError("Please select a time slot."); return false;
        }
        if (adultField.getText().isBlank() &&
                seniorField.getText().isBlank() &&
                studentField.getText().isBlank() &&
                familyField.getText().isBlank()) {
            showError("Please enter at least one passenger count."); return false;
        }
        return true;
    }

    private String generateRandomStatus() {
        String[] statuses = {"Preparing", "Seating", "Ongoing"};
        Random r = new Random();
        return statuses[r.nextInt(statuses.length)];
    }

    private String getOrDefault(String v) {
        return (v == null || v.isBlank()) ? "-" : v;
    }

    private void saveTicketToFile(String id, String ride, String date, String time,
                                  String a, String s, String st, String f, String status) throws IOException {
        try (FileWriter w = new FileWriter(TICKET_FILE, true)) {
            w.write(String.format(
                    "%s | Email: %s | Ride: %s | Date: %s | Time: %s | Adults: %s | Senior/Oku: %s | Students: %s | Family Packages: %s | Status: %s\n",
                    id, loggedInEmail, ride, date, time, a, s, st, f, status
            ));
        }
    }

    private String generateTicketId() {
        Random r = new Random();
        int num = r.nextInt(900) + 100;
        char c = (char)('A' + r.nextInt(26));
        return "TICKET-" + num + c;
    }

    private void showError(String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    @FXML protected void onChatWithUsClick() throws Exception {
        MainApp.changeScene("/fxml/chatbot.fxml");
    }

    // --- Global navigation handlers ---
    @FXML protected void goHome()          throws Exception { MainApp.changeScene("/fxml/home.fxml"); }
    @FXML protected void goBookingHistory()throws Exception { MainApp.changeScene("/fxml/booking_history.fxml"); }
    @FXML protected void goFAQ()           throws Exception { MainApp.changeScene("/fxml/faq.fxml"); }
    @FXML protected void goOurRides()      throws Exception { MainApp.changeScene("/fxml/our_rides.fxml"); }
    @FXML protected void goInfosUpdates()  throws Exception { MainApp.changeScene("/fxml/infos_updates.fxml"); }
    @FXML protected void goBookRides()     throws Exception { MainApp.changeScene("/fxml/book_rides.fxml"); }

}
