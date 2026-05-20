package controller;

import com.example.app.MainApp;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Random;

public class RideAvailabilityController {

    @FXML private DatePicker datePickerRide1, datePickerRide2, datePickerRide3, datePickerRide4;
    @FXML private Label availableLabelRide1, availableLabelRide2, availableLabelRide3, availableLabelRide4;
    @FXML private VBox seatsVBoxRide1, seatsVBoxRide2, seatsVBoxRide3, seatsVBoxRide4;

    private final String[] timeSlots = {
            "10:00am - 12:00pm",
            "1:00pm - 3:00pm",
            "4:00pm - 6:00pm",
            "7:00pm - 9:00pm"
    };

    private void updateSeats(VBox container, String date) {
        container.getChildren().clear();
        Random rnd = new Random();
        for (String slot : timeSlots) {
            int left = rnd.nextInt(51); // 0..50
            HBox row = new HBox(30);
            row.setAlignment(Pos.CENTER_LEFT);

            Label timeLbl = new Label(slot);
            timeLbl.setStyle("-fx-font-weight:bold;-fx-font-size:16;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Label seatLbl = new Label(left + " seats left");
            seatLbl.setStyle("-fx-font-size:16;");

            row.getChildren().addAll(timeLbl, spacer, seatLbl);
            container.getChildren().add(row);
        }
    }

    @FXML protected void onDateSelectedRide1() {
        if (datePickerRide1.getValue() != null) {
            String d = datePickerRide1.getValue().toString();
            availableLabelRide1.setText("Available rides on " + d);
            updateSeats(seatsVBoxRide1, d);
        }
    }
    @FXML protected void onDateSelectedRide2() {
        if (datePickerRide2.getValue() != null) {
            String d = datePickerRide2.getValue().toString();
            availableLabelRide2.setText("Available rides on " + d);
            updateSeats(seatsVBoxRide2, d);
        }
    }
    @FXML protected void onDateSelectedRide3() {
        if (datePickerRide3.getValue() != null) {
            String d = datePickerRide3.getValue().toString();
            availableLabelRide3.setText("Available rides on " + d);
            updateSeats(seatsVBoxRide3, d);
        }
    }
    @FXML protected void onDateSelectedRide4() {
        if (datePickerRide4.getValue() != null) {
            String d = datePickerRide4.getValue().toString();
            availableLabelRide4.setText("Available rides on " + d);
            updateSeats(seatsVBoxRide4, d);
        }
    }

    @FXML protected void onBookSeatClick() throws Exception {
        MainApp.changeScene("/fxml/book_rides.fxml");
    }

    // --- Global navigation handlers ---
    @FXML protected void goHome()          throws Exception { MainApp.changeScene("/fxml/home.fxml"); }

    @FXML protected void goBookingHistory()throws Exception { MainApp.changeScene("/fxml/booking_history.fxml"); }
    @FXML protected void goFAQ()           throws Exception { MainApp.changeScene("/fxml/faq.fxml"); }
    @FXML protected void goOurRides()      throws Exception { MainApp.changeScene("/fxml/our_rides.fxml"); }
    @FXML protected void goInfosUpdates()  throws Exception { MainApp.changeScene("/fxml/infos_updates.fxml"); }
    @FXML protected void goBookRides()     throws Exception { MainApp.changeScene("/fxml/book_rides.fxml"); }
}
