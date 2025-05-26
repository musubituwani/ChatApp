/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package authentication;

import static chatapp.ChatApp.users;
import chatapp.Message;
import chatapp.MessageHandler;
import javax.swing.JOptionPane;

/**
 *
 * @author RC_Student_lab
 */
public class Login {
    // User Login
    public static void login() {
        String username = JOptionPane.showInputDialog(null, "Enter username:");
        if (username == null) {
            return;
        }

        String password = JOptionPane.showInputDialog(null, "Enter password:");
        if (password == null) {
            return;
        }

        if (users.containsKey(username) && users.get(username).equals(password)) {
            JOptionPane.showMessageDialog(null, "Login successful! Welcome, " + username + "!");
            // Show the main menu after successful login
            mainMenu(username);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
        }
    }
    
    // Main Menu after Login
    private static void mainMenu(String username) {
        while (true) {
            String[] options = {"See Recent Messages", "Send Message", "Quit"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Welcome " + username + "!",
                    "Main Menu",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0 ->
                    MessageHandler.showRecentMessages(username); // Show recent messages
                case 1 ->
                    MessageHandler.sendMessage(username); // Send message
                case 2 -> {
                    JOptionPane.showMessageDialog(null, "Goodbye " + username + "!");
                    return; // Quit the app after a successful logout
                }
                default ->
                    JOptionPane.showMessageDialog(null, "Invalid choice. Please try again.");
            }
        }
    }
}
