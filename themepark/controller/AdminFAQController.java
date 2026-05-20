package controller;

import com.example.app.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class AdminFAQController {

    @FXML private BorderPane rootPane;
    @FXML private ScrollPane faqScroll;
    @FXML private GridPane  faqContainer;
    @FXML private ComboBox<String> rideComboBox;

    private TextArea editor;

    // external, writable file
    private static final Path FAQ_FILE = Paths.get("docs", "faq.txt");

    @FXML
    private void initialize() {
        try {
            ensureExternalFaq();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        loadFAQs();
    }

    /**
     * On first run, copy the bundled resource /docs/faq.txt
     * out to docs/faq.txt on the filesystem so we can edit it.
     */
    private void ensureExternalFaq() throws IOException {
        if (Files.notExists(FAQ_FILE)) {
            Files.createDirectories(FAQ_FILE.getParent());
            try (InputStream in = getClass().getResourceAsStream("/docs/faq.txt")) {
                if (in == null) {
                    throw new IOException("Bundled /docs/faq.txt missing!");
                }
                Files.copy(in, FAQ_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private void loadFAQs() {
        faqContainer.getChildren().clear();

        List<String> lines;
        try {
            lines = Files.readAllLines(FAQ_FILE, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int col = 0, row = 0;
        for (String line : lines) {
            // ← catch either delimiter
            String[] parts = line.split("[|:]", 2);
            if (parts.length < 2) continue;

            String question = parts[0].trim();
            String answer   = parts[1].trim();

            Label qLabel = new Label("Q: " + question);
            qLabel.setWrapText(true);
            qLabel.setStyle("-fx-font-weight:bold; -fx-text-fill:#2a73f0; -fx-font-size:16px;");

            Label aLabel = new Label("A: " + answer);
            aLabel.setWrapText(true);
            aLabel.setStyle("-fx-font-size:14px; -fx-padding:0 0 20 0;");

            faqContainer.add(qLabel, col, row);
            faqContainer.add(aLabel, col, row + 1);

            if ((col = (col + 1) % 2) == 0) row += 2;
        }
    }


    @FXML
    private void onEditFAQ() {
        String content;
        try {
            content = Files.readString(FAQ_FILE, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        editor = new TextArea(content);
        editor.setWrapText(true);
        editor.setPrefRowCount(20);

        Button save = new Button("Save");
        save.setOnAction(evt -> {
            try {
                Files.writeString(FAQ_FILE, editor.getText(), StandardCharsets.UTF_8);
                restoreFAQView();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Button cancel = new Button("Cancel");
        cancel.setOnAction(evt -> restoreFAQView());

        HBox controls = new HBox(10, save, cancel);
        controls.setStyle("-fx-padding:10; -fx-alignment:center;");

        VBox editPane = new VBox(10, editor, controls);
        editPane.setStyle("-fx-padding:20;");

        rootPane.setCenter(editPane);
    }

    private void restoreFAQView() {
        editor = null;
        loadFAQs();
        rootPane.setCenter(faqScroll);
    }

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

    @FXML protected void handleHome() throws Exception {
        try {
            MainApp.changeScene("/fxml/AdminHome.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error appropriately - you could show an alert here
        }    }

    @FXML protected void handleFaq() throws Exception {
        try {
            MainApp.changeScene("/fxml/Adminfaq.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error appropriately - you could show an alert here
        }    }

    @FXML protected void handleViewBookingHistories() throws Exception {
        try {
            MainApp.changeScene("/fxml/booking_history.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error appropriately - you could show an alert here
        }
    }

    @FXML protected void handleAdminBookRides() throws Exception {
        try {
            MainApp.changeScene("/fxml/AdminBookRides.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error appropriately - you could show an alert here
        }    }
}
