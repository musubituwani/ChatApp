package chatapp;

import authentication.Login;
import authentication.Registration;
import java.util.HashMap;
import javax.swing.*;

public class ChatApp {

    
    // Stores username and password
    public static final HashMap<String, String> users = new HashMap<>();
    

    

    public static void main(String[] args) {
        Registration.loadUserData(); // Load existing user data from the file
        MessageHandler.loadMessages(); // Load recent messages from the file

        while (true) {
            String[] options = {"Register", "Login", "Exit"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Welcome to QuickChat App!",
                    "Chat App",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                // Register
                case 0 ->
                    Registration.register();
                // Login
                case 1 ->
                    Login.login();
                // Exit
                case 2 -> {
                    JOptionPane.showMessageDialog(null, "Goodbye!");
                    // Save user data to the file before exiting
                    Registration.saveUserData();
                    System.exit(0);
                }
                default ->
                    JOptionPane.showMessageDialog(null, "Invalid choice. Please try again.");
            }
        }
    }

}   
    

    

    