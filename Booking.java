package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Booking {
    private final SimpleStringProperty email;
    private final SimpleStringProperty rideName;
    private final SimpleStringProperty date;
    private final SimpleStringProperty time;
    private final SimpleStringProperty status;

    public Booking(String email, String rideName, String date, String time, String status) {
        this.email    = new SimpleStringProperty(email);
        this.rideName = new SimpleStringProperty(rideName);
        this.date     = new SimpleStringProperty(date);
        this.time     = new SimpleStringProperty(time);
        this.status   = new SimpleStringProperty(status);
    }

    // Getters
    public String getEmail()    { return email.get(); }
    public String getRideName() { return rideName.get(); }
    public String getDate()     { return date.get(); }
    public String getTime()     { return time.get(); }
    public String getStatus()   { return status.get(); }

    // Setter for status (enables editing)
    public void setStatus(String newStatus) {
        this.status.set(newStatus);
    }
}


