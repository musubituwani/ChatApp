
package authentication;

import static chatapp.ChatApp.users;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 *
 * @author RC_Student_lab
 */
public class Registration {
    // File to store user data
    private static final String DATA_FILE = "users.txt";
    // Stores username and phone number
    private static final HashMap<String, String> phoneNumbers = new HashMap<>();
    // User Registration
    public static void register() {
        String username = getValidUsername();
        if (username == null) {
            return; // Cancelled
        }
        String password = getValidPassword();
        if (password == null) {
            return;
        }

        String phoneNumber = getValidPhoneNumber();
        if (phoneNumber == null) {
            return;
        }

        if (users.containsKey(username)) {
            JOptionPane.showMessageDialog(null, "Username already exists. Please choose a different username.");
        } else {
            users.put(username, password);
            phoneNumbers.put(username, phoneNumber);
            JOptionPane.showMessageDialog(null, "Registration successful!");
        }
    }
    // Save user data to the file
    public static void saveUserData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (String username : users.keySet()) {
                String password = users.get(username);
                String phoneNumber = phoneNumbers.get(username);
                writer.write(username + "," + password + "," + phoneNumber);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving user data file.");
        }
    }
    // Load user data from the file
    public static void loadUserData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    String phoneNumber = parts[2];
                    users.put(username, password);
                    phoneNumbers.put(username, phoneNumber);
                }
            }
        } catch (FileNotFoundException e) {
            // File not found, no data to load
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading user data file.");
        }
    }
    // Get a valid username for registration
    private static String getValidUsername() {
        return JOptionPane.showInputDialog(null, "Enter username (must contain an underscore and be no more than 5 characters):");
    }

    // Get a valid password for registration
    private static String getValidPassword() {
        return JOptionPane.showInputDialog(null, "Enter password (Min 8 chars, 1 uppercase, 1 number, 1 special character):");
    }

    // Get a valid phone number for registration
    private static String getValidPhoneNumber() {
        return JOptionPane.showInputDialog(null, "Enter South African phone number (e.g., +27821234567):");
    }

}
