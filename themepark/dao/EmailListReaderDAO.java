package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmailListReaderDAO {

    public static List<String> readEmails(String filePath) {
        List<String> emails = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Remove whitespace
                line = line.trim();
                if (!line.isEmpty()) {
                    emails.add(line.split(":")[0]); // If no password, just use full line
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emails;
    }
}