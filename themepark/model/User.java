package model;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String role;

    public User(String firstName, String lastName, String email, String password, String phoneNumber, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRole() { return role; }

    // Convert to CSV for saving
    public String toCSV() {
        return firstName + "," + lastName + "," + email + "," + password + "," + phoneNumber + "," + role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
