package controller;

import com.example.app.MainApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import dao.UserDAO;
import javafx.stage.Stage;
import model.User;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class AdminBookRidesController implements Initializable {

    @FXML private ComboBox<String> rideComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private TextField adultField, seniorField, studentField, familyField;
    @FXML private ComboBox<String> userComboBox;
    @FXML private Button checkAvailabilityButton;
    @FXML private Button bookButton;

    private final String ADMIN_TICKET_FILE = "admin_tickets.txt";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupEventHandlers();
        loadUsersFromFile();
    }

    private void setupComboBoxes() {
        // Setup ride options
        ObservableList<String> rides = FXCollections.observableArrayList(
                "Ride 1", "Ride 2", "Ride 3", "Ride 4"
        );
        rideComboBox.setItems(rides);

        // Setup time slots with the requested time ranges
        ObservableList<String> timeSlots = FXCollections.observableArrayList(
                "10:00am - 12:00pm",
                "1:00pm - 3:00pm",
                "4:00pm - 6:00pm",
                "7:00pm - 9:00pm"
        );
        timeComboBox.setItems(timeSlots);

        // Set default values for passenger fields
        adultField.setText("0");
        seniorField.setText("0");
        studentField.setText("0");
        familyField.setText("0");
    }

    private void loadUsersFromFile() {
        ObservableList<String> users = FXCollections.observableArrayList();

        try {
            // Use UserDAO to get all users
            List<User> allUsers = UserDAO.getAllUsers();
            int userIdCounter = 1001;

            for (User user : allUsers) {
                // Only add non-admin users to the list
                if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
                    String fullName = user.getFirstName() + " " + user.getLastName();
                    users.add(fullName + " (ID: " + userIdCounter + ")");
                    userIdCounter++;
                }
            }

            if (users.isEmpty()) {
                users.add("No users found");
            }

        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            // Fallback to mock users if file can't be read
            users.addAll(FXCollections.observableArrayList(
                    "John Doe (ID: 1001)", "Jane Smith (ID: 1002)", "Mike Johnson (ID: 1003)",
                    "Sarah Wilson (ID: 1004)", "David Brown (ID: 1005)", "Lisa Davis (ID: 1006)",
                    "Tom Anderson (ID: 1007)", "Emma Taylor (ID: 1008)", "Chris Martin (ID: 1009)",
                    "Anna Garcia (ID: 1010)"
            ));
            showError("Could not load users from file. Using default users.");
        }

        userComboBox.setItems(users);
    }

    private void setupEventHandlers() {
        checkAvailabilityButton.setOnAction(e -> onCheckRideAvailability());
        bookButton.setOnAction(e -> onBookRideForUser());
    }

    @FXML
    protected void onCheckRideAvailability() {
        try {
            if (rideComboBox.getValue() == null || datePicker.getValue() == null || timeComboBox.getValue() == null) {
                showError("Please select ride, date, and time to check availability.");
                return;
            }

            String ride = rideComboBox.getValue();
            String date = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String time = timeComboBox.getValue();

            // Mock availability check - in real implementation, this would query a database
            Random random = new Random();
            int availableSlots = random.nextInt(50) + 10; // Random availability between 10-60 slots

            String availabilityInfo = String.format(
                    "Ride Availability Check\n\n" +
                            "Ride: %s\n" +
                            "Date: %s\n" +
                            "Time: %s\n" +
                            "Available Slots: %d\n\n" +
                            "Status: %s",
                    ride, date, time, availableSlots,
                    availableSlots > 20 ? "Good Availability" : availableSlots > 10 ? "Limited Availability" : "Low Availability"
            );

            showInfo(availabilityInfo);
        } catch (Exception e) {
            showError("Error checking availability: " + e.getMessage());
        }
    }

    @FXML
    protected void onBookRideForUser() {
        try {
            if (!validateAdminForm()) return;

            String ride = rideComboBox.getValue();
            String date = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String time = timeComboBox.getValue();
            String selectedUser = userComboBox.getValue();

            // Check if no users found
            if ("No users found".equals(selectedUser)) {
                showError("No users available for booking.");
                return;
            }

            // Extract user ID from the selected user string
            String userId = extractUserIdFromSelection(selectedUser);
            String userName = extractUserNameFromSelection(selectedUser);

            String adults = getOrDefault(adultField.getText());
            String seniors = getOrDefault(seniorField.getText());
            String students = getOrDefault(studentField.getText());
            String families = getOrDefault(familyField.getText());

            String adminTicketId = generateAdminTicketId();

            // Calculate total passengers
            int totalPassengers = calculateTotalPassengers(adults, seniors, students, families);

            String ticketSummary = String.format(
                    "ADMIN BOOKING CONFIRMATION\n\n" +
                            "Ticket ID: %s\n" +
                            "Booked by: ADMIN\n" +
                            "For User: %s (ID: %s)\n" +
                            "Ride: %s\n" +
                            "Date: %s\n" +
                            "Time: %s\n" +
                            "Adults: %s\n" +
                            "Senior/Oku: %s\n" +
                            "Students: %s\n" +
                            "Family Packages: %s\n" +
                            "Total Passengers: %d\n" +
                            "Status: Confirmed by Admin\n" +
                            "File saved to: %s",
                    adminTicketId, userName, userId, ride, date, time,
                    adults, seniors, students, families, totalPassengers,
                    ADMIN_TICKET_FILE
            );

            saveAdminTicketToFile(adminTicketId, selectedUser, ride, date, time, adults, seniors, students, families);

            // Send email notification to the user
            String userEmail = getUserEmailById(userId);
            if (userEmail != null && !userEmail.isEmpty()) {
                boolean emailSent = AutoEmailSenderController.sendBookingEmail(
                        userEmail, adminTicketId, ride, date, time,
                        adults, seniors, students, families, "Confirmed by Admin"
                );

                if (emailSent) {
                    showInfo("Admin Booking Successful!\n\n" + ticketSummary + "\n\nEmail notification sent to: " + userEmail);
                } else {
                    showInfo("Admin Booking Successful!\n\n" + ticketSummary + "\n\nWarning: Could not send email notification to user.");
                }
            } else {
                showInfo("Admin Booking Successful!\n\n" + ticketSummary + "\n\nWarning: User email not found, no notification sent.");
            }

            // Optional: Clear form after successful booking
            clearForm();

        } catch (Exception e) {
            showError("An error occurred during admin booking: " + e.getMessage());
        }
    }

    /**
     * Retrieves the user's email address by their ID from the users.txt file
     */
    private String getUserEmailById(String userId) {
        try {
            List<User> allUsers = UserDAO.getAllUsers();
            int currentUserId = 1001;

            for (User user : allUsers) {
                // Only process non-admin users
                if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
                    if (String.valueOf(currentUserId).equals(userId)) {
                        return user.getEmail();
                    }
                    currentUserId++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading user email: " + e.getMessage());
        }
        return null;
    }

    private boolean validateAdminForm() {
        if (rideComboBox.getValue() == null) {
            showError("Please select a ride.");
            return false;
        }
        if (datePicker.getValue() == null) {
            showError("Please select a date.");
            return false;
        }
        if (timeComboBox.getValue() == null) {
            showError("Please select a time slot.");
            return false;
        }
        if (userComboBox.getValue() == null) {
            showError("Please select a user for whom to book the ticket.");
            return false;
        }

        // Check if at least one passenger type is selected
        if (isAllFieldsEmpty()) {
            showError("Please enter at least one passenger count.");
            return false;
        }

        // Validate passenger counts are numeric
        if (!validatePassengerCounts()) {
            showError("Please enter valid numbers for passenger counts.");
            return false;
        }

        return true;
    }

    private boolean isAllFieldsEmpty() {
        return (adultField.getText().isBlank() || adultField.getText().equals("0")) &&
                (seniorField.getText().isBlank() || seniorField.getText().equals("0")) &&
                (studentField.getText().isBlank() || studentField.getText().equals("0")) &&
                (familyField.getText().isBlank() || familyField.getText().equals("0"));
    }

    private boolean validatePassengerCounts() {
        try {
            if (!adultField.getText().isBlank()) Integer.parseInt(adultField.getText());
            if (!seniorField.getText().isBlank()) Integer.parseInt(seniorField.getText());
            if (!studentField.getText().isBlank()) Integer.parseInt(studentField.getText());
            if (!familyField.getText().isBlank()) Integer.parseInt(familyField.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int calculateTotalPassengers(String adults, String seniors, String students, String families) {
        int total = 0;
        if (!adults.equals("-")) total += Integer.parseInt(adults);
        if (!seniors.equals("-")) total += Integer.parseInt(seniors);
        if (!students.equals("-")) total += Integer.parseInt(students);
        if (!families.equals("-")) total += Integer.parseInt(families) * 4; // Assuming family package = 4 people
        return total;
    }

    private String extractUserIdFromSelection(String selection) {
        // Extract ID from format "Name (ID: 1001)"
        int startIndex = selection.indexOf("ID: ") + 4;
        int endIndex = selection.indexOf(")", startIndex);
        return selection.substring(startIndex, endIndex);
    }

    private String extractUserNameFromSelection(String selection) {
        // Extract name from format "Name (ID: 1001)"
        int endIndex = selection.indexOf(" (ID:");
        return selection.substring(0, endIndex);
    }

    private String getOrDefault(String value) {
        return (value == null || value.isBlank() || value.equals("0")) ? "-" : value;
    }

    private void saveAdminTicketToFile(String ticketId, String user, String ride, String date, String time,
                                       String adults, String seniors, String students, String families) throws IOException {
        try (FileWriter writer = new FileWriter(ADMIN_TICKET_FILE, true)) {
            writer.write(String.format(
                    "%s | Admin Booking | User: %s | Ride: %s | Date: %s | Time: %s | Adults: %s | Senior/Oku: %s | Students: %s | Family Packages: %s | Status: Admin Confirmed | Timestamp: %s\n",
                    ticketId, user, ride, date, time, adults, seniors, students, families,
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    private String generateAdminTicketId() {
        Random random = new Random();
        int number = random.nextInt(900) + 100;
        char letter = (char)('A' + random.nextInt(26));
        return "ADMIN-" + number + letter;
    }

    private void clearForm() {
        rideComboBox.setValue(null);
        datePicker.setValue(null);
        timeComboBox.setValue(null);
        userComboBox.setValue(null);
        adultField.setText("0");
        seniorField.setText("0");
        studentField.setText("0");
        familyField.setText("0");
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Admin Booking Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Admin Booking Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Utility to load a new FXML into the primary stage.
     */
    private void switchScene(String fxmlPath) {
        try {
            // grab the window from any injected control
            Stage stage = (Stage) rideComboBox.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxmlPath)));
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    // --- Global navigation handlers ---
    /** Handler for the HOME button */
    @FXML
    private void handleHome(ActionEvent event) {
        try {
            // Change this path to whatever your Admin‐home FXML is actually named
            MainApp.changeScene("/fxml/AdminHome.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            // optionally show an alert here
        }
    }


    @FXML
    private void handleFaq(ActionEvent event) {
        try {
            MainApp.changeScene("/fxml/Adminfaq.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error appropriately - you could show an alert here
        }
    }

    @FXML
    private void goHistory() {
        // implement as needed
    }

    @FXML
    private void goMakeBooking() {
        switchScene("/fxml/AdminBookRides.fxml");
    }
    // --- Navigation handlers ---
    @FXML protected void handleHome() throws Exception {
        // Main.changeScene("/com/example/adminbookrides/admin_home.fxml");
    }

    @FXML protected void handleFaq() throws Exception {
        // Main.changeScene("/com/example/adminbookrides/admin_faq.fxml");
    }

    @FXML protected void handleViewBookingHistories() throws Exception {
        try {
            MainApp.changeScene("/fxml/booking_history.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error appropriately - you could show an alert here
        }
    }

    @FXML protected void handleAdminBookRides() throws Exception {
        // Already on this page, no navigation needed
    }

// Remove the old navigation methods and replace with the above
}