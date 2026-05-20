package controller;

import com.example.app.MainApp;
import controller.AutoEmailSenderController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import model.Booking;
import util.Session;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ViewBookingHistoryController {

    @FXML private FlowPane cardContainer;
    @FXML private TableView<Booking> adminTable;
    @FXML private TableColumn<Booking, String> emailCol, rideNameCol, dateCol, timeCol, statusCol;
    @FXML private Button editButton, saveButton;
    @FXML private HBox editSaveButton;
    @FXML private HBox adminNavbar, userNavbar;
    @FXML private VBox userInfoBox;
    @FXML private Label nameLabel, emailLabel, phoneLabel;

    private static final String TICKET_FILE = "combined-javafx/tickets.txt";
    private static final List<String> STATUS_OPTIONS = List.of("Preparing", "Ongoing", "Seating");

    // Snapshot of statuses when Edit is clicked
    private final List<String> originalStatuses = new ArrayList<>();

    public void initialize() {
        String loggedEmail = Session.loggedInEmail;
        if (loggedEmail == null) return;

        setupTableColumns();
        configureButtons();

        String role = getUserTypeByEmail(loggedEmail);
        if ("admin".equals(role)) {
            showAdminView();
            loadAllBookings();
        } else {
            showUserView();
            displayUserInfo(loggedEmail);
            loadUserBookings(loggedEmail);
        }
    }

    private void configureButtons() {
        editButton.setDisable(true);
        saveButton.setDisable(true);
    }

    private void showAdminView() {
        adminNavbar .setVisible(true);  adminNavbar .setManaged(true);
        userNavbar  .setVisible(false); userNavbar  .setManaged(false);
        adminTable  .setVisible(true);  adminTable  .setManaged(true);
        cardContainer.setVisible(false); cardContainer.setManaged(false);

        editButton.setDisable(false);
        saveButton.setDisable(true);
    }

    private void showUserView() {
        adminNavbar .setVisible(false);  adminNavbar .setManaged(false);
        userNavbar  .setVisible(true);   userNavbar  .setManaged(true);
        adminTable  .setVisible(false);  adminTable  .setManaged(false);
        cardContainer.setVisible(true);  cardContainer.setManaged(true);

        editSaveButton.setVisible(false);
    }

    private void setupTableColumns() {
        emailCol   .setCellValueFactory(b -> new SimpleStringProperty(b.getValue().getEmail()));
        rideNameCol.setCellValueFactory(b -> new SimpleStringProperty(b.getValue().getRideName()));
        dateCol    .setCellValueFactory(b -> new SimpleStringProperty(b.getValue().getDate()));
        timeCol    .setCellValueFactory(b -> new SimpleStringProperty(b.getValue().getTime()));
        statusCol  .setCellValueFactory(b -> new SimpleStringProperty(b.getValue().getStatus()));
        adminTable .setEditable(false);
    }

    @FXML
    private void handleEditBooking(ActionEvent evt) {
        // Capture original statuses
        originalStatuses.clear();
        for (Booking b : adminTable.getItems()) {
            originalStatuses.add(b.getStatus());
        }

        adminTable.setEditable(true);
        statusCol.setCellFactory(col -> new TableCell<Booking,String>() {
            private final ComboBox<String> combo = new ComboBox<>(FXCollections.observableArrayList(STATUS_OPTIONS));
            {
                combo.setMaxWidth(Double.MAX_VALUE);
                combo.setOnAction(e -> {
                    Booking bk = getTableView().getItems().get(getIndex());
                    if (bk != null) bk.setStatus(combo.getValue());
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    combo.setValue(item);
                    setGraphic(combo);
                }
            }
        });

        editButton.setDisable(true);
        saveButton.setDisable(false);
    }

    @FXML
    private void handleSaveBooking(ActionEvent evt) {
        try {
            List<String> lines = Files.readAllLines(Path.of(TICKET_FILE));
            ObservableList<Booking> items = adminTable.getItems();
            int idx = 0;

            for (int i = 0; i < lines.size() && idx < items.size(); i++) {
                String line = lines.get(i);
                if (!line.contains("Email:")) continue;

                Booking bk      = items.get(idx);
                String oldStat = originalStatuses.get(idx);
                String newStat = bk.getStatus();

                // Extract Ticket ID
                String ticketId = line.split("\\|")[0].trim();

                // Send status-change email if status changed
                if (!oldStat.equals(newStat)) {
                    AutoEmailSenderController.sendStatusChangeEmail(
                            bk.getEmail(),
                            ticketId,
                            bk.getRideName(),
                            oldStat,
                            newStat
                    );
                }

                // Update the line’s Status:
                String[] parts = line.split("\\|");
                for (int j = 0; j < parts.length; j++) {
                    if (parts[j].trim().startsWith("Status:")) {
                        parts[j] = " Status: " + newStat;
                        break;
                    }
                }
                lines.set(i, String.join(" |", parts));
                idx++;
            }

            Files.write(Path.of(TICKET_FILE), lines, StandardOpenOption.TRUNCATE_EXISTING);

            // Restore read-only
            adminTable.setEditable(false);
            setupTableColumns();
            adminTable.refresh();

            editButton.setDisable(false);
            saveButton.setDisable(true);

            new Alert(Alert.AlertType.INFORMATION,
                    "Statuses saved & emails sent!",
                    ButtonType.OK).showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Error saving/emailing:\n" + ex.getMessage(),
                    ButtonType.OK).showAndWait();
        }
    }



    private void loadAllBookings() {
        adminTable.getItems().clear();
        try (BufferedReader br = new BufferedReader(new FileReader(TICKET_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String em    = extractValue(line, "Email:");
                String ride  = extractValue(line, "Ride:");
                String d     = extractValue(line, "Date:");
                String t     = extractValue(line, "Time:");
                String stat  = extractValue(line, "Status:");
                if (!em.isEmpty()) {
                    adminTable.getItems().add(new Booking(em, ride, d, t, stat));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserBookings(String email) {
        cardContainer.getChildren().clear();
        try (BufferedReader br = new BufferedReader(new FileReader(TICKET_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String e = extractValue(line, "Email:");
                if (!e.equals(email)) continue;

                String ride     = extractValue(line, "Ride:");
                String d        = extractValue(line, "Date:");
                String t        = extractValue(line, "Time:");
                String stat     = extractValue(line, "Status:");
                String adults   = extractValue(line, "Adults:");
                String seniors  = extractValue(line, "Senior/Oku:");
                String students = extractValue(line, "Students:");
                String fam      = extractValue(line, "Family Packages:");

                VBox card = createBookingCard(ride, d, t, stat, adults, seniors, students, fam);
                cardContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayUserInfo(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader("combined-javafx/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[2].equals(email)) {
                    nameLabel .setText("Name: "  + parts[0]);
                    emailLabel.setText("Email: " + email);
                    phoneLabel.setText("Phone: " + parts[3]);
                    userInfoBox.setVisible(true);
                    userInfoBox.setManaged(true);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getUserTypeByEmail(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader("combined-javafx/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[2].equals(email)) {
                    return parts[5];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "user";
    }

    private VBox createBookingCard(String ride, String date, String time, String status,
                                   String adults, String seniors, String students, String families) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setPrefWidth(200);
        card.setStyle("-fx-border-color:black; -fx-border-radius:10; -fx-background-radius:10;");
        card.setBackground(new Background(new BackgroundFill(Color.rgb(245,245,245), new CornerRadii(10), Insets.EMPTY)));

        Label title = new Label("Ride: " + ride);
        title.setFont(Font.font(14));
        title.setStyle("-fx-font-weight:bold;");

        card.getChildren().addAll(
                title,
                new Label("Date: "   + date),
                new Label("Time: "   + time),
                new Label("Status: " + status),
                new Label("Adults: " + adults),
                new Label("Senior/OKU: " + seniors),
                new Label("Students: "   + students),
                new Label("Families: "   + families)
        );
        return card;
    }

    private String extractValue(String line, String key) {
        for (String part : line.split("\\|")) {
            part = part.trim();
            if (part.startsWith(key)) {
                return part.substring(key.length()).trim();
            }
        }
        return "";
    }

    // Navigation methods…
    @FXML protected void goHome()           throws Exception { MainApp.changeScene("/fxml/home.fxml"); }
    @FXML protected void goBookingHistory() throws Exception { MainApp.changeScene("/fxml/booking_history.fxml"); }
    @FXML protected void goFAQ()            throws Exception { MainApp.changeScene("/fxml/faq.fxml"); }
    @FXML protected void goOurRides()       throws Exception { MainApp.changeScene("/fxml/our_rides.fxml"); }
    @FXML protected void goInfosUpdates()   throws Exception { MainApp.changeScene("/fxml/infos_updates.fxml"); }
    @FXML protected void goBookRides()      throws Exception { MainApp.changeScene("/fxml/book_rides.fxml"); }
    @FXML protected void handleHome()      throws Exception { MainApp.changeScene("/fxml/AdminHome.fxml"); }
    @FXML protected void handleFaq()      throws Exception { MainApp.changeScene("/fxml/Adminfaq.fxml"); }
    @FXML protected void handleAdminBookRides()      throws Exception { MainApp.changeScene("/fxml/AdminBookRides.fxml"); }

}
