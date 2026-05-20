package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.User;

public class UserDAO {
    private static final String FILE_NAME = "combined-javafx/users.txt";

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            // ← This is where your “Example CSV parsing” goes:
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",");
                // assuming order: firstName,lastName,email,password,phone,role
                if (cols.length == 6) {
                    User u = new User(
                            cols[0].trim(), // firstName
                            cols[1].trim(), // lastName
                            cols[2].trim(), // email
                            cols[3].trim(), // password
                            cols[4].trim(), // phoneNumber
                            cols[5].trim()  // role
                    );
                    users.add(u);
                }
            }
        } catch (IOException e) {
            System.out.println("users.txt not found. Creating a new one.");
            createEmptyFile();
        }
        return users;
    }


    public static void addUser(User user) {
        try {
            File file = new File(FILE_NAME);
            boolean newLineNeeded = file.exists() && file.length() > 0;

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                if (newLineNeeded) {
                    bw.newLine(); // Add newline if file is not empty
                }
                bw.write(user.toCSV());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createEmptyFile() {
        try {
            new File(FILE_NAME).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
