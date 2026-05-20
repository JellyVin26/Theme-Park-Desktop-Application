    package controller;

    import com.example.app.MainApp;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Scene;
    import javafx.scene.control.Label;
    import javafx.scene.layout.GridPane;
    import javafx.stage.Stage;

    import java.io.IOException;
    import java.io.InputStream;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Scanner;

    public class FAQController {

        @FXML
        private GridPane faqContainer;

        @FXML
        private void initialize() {
            loadFAQs();
        }

        private void loadFAQs() {
            faqContainer.getChildren().clear();

            List<String> lines;

            Path external = Paths.get("docs", "faq.txt");
            if (Files.exists(external)) {
                // 1) Read the admin‐edited external file
                try {
                    lines = Files.readAllLines(external, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                // 2) Fall back to the bundled resource
                InputStream in = getClass().getResourceAsStream("/docs/faq.txt");
                if (in == null) {
                    System.err.println("faq.txt not found in resources");
                    return;
                }
                lines = new ArrayList<>();
                try (Scanner sc = new Scanner(in, StandardCharsets.UTF_8)) {
                    while (sc.hasNextLine()) {
                        lines.add(sc.nextLine());
                    }
                }
            }

            int col = 0, row = 0;
            for (String line : lines) {
                String[] parts = line.split("[|:]", 2);
                if (parts.length < 2) continue;

                String question = parts[0].trim();
                String answer   = parts[1].trim();

                Label qLabel = new Label("Q: " + question);
                qLabel.setStyle(
                        "-fx-font-weight: bold; " +
                                "-fx-text-fill: #2a73f0; " +
                                "-fx-font-size: 16px;"
                );
                qLabel.setWrapText(true);

                Label aLabel = new Label("A: " + answer);
                aLabel.setStyle("-fx-font-size: 14px; -fx-padding: 0 0 20 0;");
                aLabel.setWrapText(true);

                faqContainer.add(qLabel, col, row);
                faqContainer.add(aLabel, col, row + 1);

                col = (col + 1) % 2;
                if (col == 0) row += 2;
            }
        }


        // --- Global navigation handlers ---
        @FXML
        private void goHome() {
            switchScene("/fxml/home.fxml");
        }



        @FXML
        private void goBookingHistory() {
            switchScene("/fxml/booking_History.fxml");
        }

        @FXML
        private void goFAQ() {
            // already here, just reload if needed
            reloadFAQ();
        }

        @FXML
        private void goOurRides() {
            switchScene("/fxml/our_rides.fxml");
        }

        @FXML
        private void goInfosUpdates() {
            switchScene("/fxml/infos_updates.fxml");}

        @FXML
        private void goBookRides() {
            switchScene("/fxml/book_rides.fxml");
        }

        @FXML
        private void reloadFAQ() {
            loadFAQs();
        }

        @FXML
        protected void onChatWithUsClick() throws Exception {
            MainApp.changeScene("/fxml/chatbot.fxml");
        }

        private void switchScene(String fxmlPath) {
            try {
                Stage stage = (Stage) faqContainer.getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxmlPath)));
                stage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
