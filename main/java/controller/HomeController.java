package controller;

import com.example.app.MainApp;
import dao.UserDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import model.User;
import util.Session;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    // —— your existing @FXML injections ——
    @FXML private ImageView   carouselImage;
    @FXML private RadioButton dot1, dot2, dot3;

    // —— new: inject the welcome label ——
    @FXML private Label       welcomeLabel;

    private User   currentUser;
    private final Image[] slides = new Image[3];
    private int    currentIndex = 0;
    private Timeline autoSlide;

    /** ← This setter is required so LoginController.openUserHome(...) compiles & works */
    public void setUser(User user) {
        this.currentUser = user;
        // update the label immediately
        welcomeLabel.setText(
                "Welcome, "
                        + user.getFullName()
                        + " (" + user.getRole() + ")"
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // --- Carousel setup (unchanged) ---
        slides[0] = new Image(getClass()
                .getResource("/docs/carousel1.jpg").toExternalForm());
        slides[1] = new Image(getClass()
                .getResource("/docs/carousel2.jpg").toExternalForm());
        slides[2] = new Image(getClass()
                .getResource("/docs/carousel3.jpg").toExternalForm());

        carouselImage.setImage(slides[0]);
        dot1.setSelected(true);

        autoSlide = new Timeline(new KeyFrame(
                Duration.seconds(5), e -> showNextSlide()));
        autoSlide.setCycleCount(Timeline.INDEFINITE);
        autoSlide.play();

        // --- Fallback welcome text if setUser wasn't called ---
        if (welcomeLabel.getText() == null || welcomeLabel.getText().isEmpty()) {
            String email = Session.loggedInEmail;
            if (email != null) {
                User u = UserDAO.getAllUsers().stream()
                        .filter(x -> x.getEmail().equals(email))
                        .findFirst()
                        .orElse(null);
                if (u != null) {
                    welcomeLabel.setText(
                            "Welcome, "
                                    + u.getFullName()
                                    + " (" + u.getRole() + ")"
                    );
                }
            }
        }
    }

    private void showNextSlide() {
        currentIndex = (currentIndex + 1) % slides.length;
        updateCarousel();
    }

    private void updateCarousel() {
        carouselImage.setImage(slides[currentIndex]);
        dot1.setSelected(currentIndex == 0);
        dot2.setSelected(currentIndex == 1);
        dot3.setSelected(currentIndex == 2);
    }

    // Manual dot click handlers
    @FXML void onDot1Click(ActionEvent e) { seekSlide(0); }
    @FXML void onDot2Click(ActionEvent e) { seekSlide(1); }
    @FXML void onDot3Click(ActionEvent e) { seekSlide(2); }
    private void seekSlide(int idx) {
        autoSlide.stop();
        currentIndex = idx;
        updateCarousel();
        autoSlide.playFromStart();
    }

    // Chatbot
    @FXML protected void onChatWithUsClick() throws Exception {
        MainApp.changeScene("/fxml/chatbot.fxml");
    }

    // Navigation links (unchanged)
    @FXML protected void goHome()           throws Exception { MainApp.changeScene("/fxml/home.fxml"); }
    @FXML protected void goBookingHistory() throws Exception { MainApp.changeScene("/fxml/booking_history.fxml"); }
    @FXML protected void goFAQ()            throws Exception { MainApp.changeScene("/fxml/faq.fxml"); }
    @FXML protected void goOurRides()       throws Exception { MainApp.changeScene("/fxml/our_rides.fxml"); }
    @FXML protected void goInfosUpdates()   throws Exception { MainApp.changeScene("/fxml/infos_updates.fxml"); }
    @FXML protected void goBookRides()      throws Exception { MainApp.changeScene("/fxml/book_rides.fxml"); }
}
