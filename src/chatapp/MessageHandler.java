package chatapp;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author RC_Student_lab
 */
public class MessageHandler {

    // List to store messages (for simplicity, stored in memory)
    private static final ArrayList<String> messages = new ArrayList<>();
    private static final String MESSAGE_FILE = "messages.json"; // Changed to .txt for text file storage

    public static void sendMessage(String username) {
        // Get recipient
        String recipient = JOptionPane.showInputDialog(null, "Enter recipient username:");
        if (recipient == null || recipient.trim().isEmpty()) {
            return; // Cancelled or empty input
        }

        // Ask if the user wants to reply to an existing message
        int replyChoice = JOptionPane.showConfirmDialog(null, "Do you want to reply to a previous message?", "Reply", JOptionPane.YES_NO_OPTION);
        String replyToMessageId = null;

        if (replyChoice == JOptionPane.YES_OPTION) {
            // Show all the messages that the user can reply to
            ArrayList<Message> allMessages = loadMessagesFromJsonFile();
            ArrayList<Message> userMessages = new ArrayList<>();
            for (Message msg : allMessages) {
                if (msg.involvesUser(username)) {
                    userMessages.add(msg);
                }
            }

            // Show the list of messages to reply to
            StringBuilder messageList = new StringBuilder("Select a message to reply to:\n");
            for (int i = 0; i < userMessages.size(); i++) {
                Message msg = userMessages.get(i);
                messageList.append(i + 1).append(". From: ").append(msg.getSender())
                        .append(" | To: ").append(msg.getRecipient())
                        .append(" | Message: ").append(msg.getContent())
                        .append(" | Sent: ").append(msg.getTimestamp())
                        .append("\n");
            }

            // Get the user's choice of message to reply to
            String replyChoiceMessage = JOptionPane.showInputDialog(null, messageList.toString());
            if (replyChoiceMessage != null && !replyChoiceMessage.trim().isEmpty()) {
                int messageIndex = Integer.parseInt(replyChoiceMessage.trim()) - 1;
                if (messageIndex >= 0 && messageIndex < userMessages.size()) {
                    replyToMessageId = userMessages.get(messageIndex).getTimestamp();
                }
            }
        }

        // Get message content
        String messageContent = JOptionPane.showInputDialog(null, "Enter your message:");
        if (messageContent == null || messageContent.trim().isEmpty()) {
            return; // Cancelled or empty input
        }

        // Get the current timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());

        // Create a Message object (include replyToMessageId if this is a reply)
        Message message = new Message(username, recipient, messageContent, timestamp, replyToMessageId);

        // Save the message to the JSON file
        saveMessageToJsonFile(message);

        // Notify the user that the message has been sent
        JOptionPane.showMessageDialog(null, "Message sent to " + recipient);
    }

    // Save message to JSON file
    private static void saveMessageToJsonFile(Message message) {
        // Read existing messages from the JSON file
        ArrayList<Message> messageList = loadMessagesFromJsonFile();

        // Add the new message
        messageList.add(message);

        // Use Gson to write the list of messages to the JSON file
        try (Writer writer = new FileWriter(MESSAGE_FILE)) {
            Gson gson = new Gson();
            gson.toJson(messageList, writer);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving messages to file.");
        }
    }
    // Save a list of messages to the JSON file

    private static void saveMessagesToJsonFile(ArrayList<Message> messageList) {
        try (Writer writer = new FileWriter(MESSAGE_FILE)) {
            Gson gson = new Gson();
            gson.toJson(messageList, writer); // Write the updated message list to the JSON file
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving messages to file.");
        }
    }

    // Method to load messages from the JSON file
    private static ArrayList<Message> loadMessagesFromJsonFile() {
        ArrayList<Message> messageList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGE_FILE))) {
            Gson gson = new Gson();
            Type messageListType = new TypeToken<ArrayList<Message>>() {
            }.getType();

            String content = reader.readLine();
            if (content != null && !content.isEmpty()) {
                messageList = gson.fromJson(content, messageListType); // Deserialize the messages
            }
        } catch (FileNotFoundException e) {
            // Handle case where the file doesn't exist yet
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading messages from file.");
        } catch (JsonSyntaxException e) {
            // Handle case where the file has invalid JSON
            JOptionPane.showMessageDialog(null, "Invalid JSON format in messages file.");
        }

        return messageList;
    }

    /**
     * public static void showRecentMessages(String username) {
     * ArrayList<Message> allMessages = loadMessagesFromJsonFile();
     *
     * // Set of unique users the logged-in user has interacted with Set<String>
     * chatPartners = new HashSet<>();
     *
     * for (Message msg : allMessages) { if (msg.getSender().equals(username)) {
     * chatPartners.add(msg.getRecipient()); } if
     * (msg.getRecipient().equals(username)) {
     * chatPartners.add(msg.getSender()); } }
     *
     * // Create a list of chat partners with clickable names StringBuilder
     * partnerList = new StringBuilder("Recent Chats:\n"); for (String partner :
     * chatPartners) { partnerList.append(partner).append("\n"); }
     *
     * // Show the dialog with clickable names (partner names) String
     * selectedPartner = (String) JOptionPane.showInputDialog( null,
     * partnerList.toString(), "Select Chat Partner", JOptionPane.PLAIN_MESSAGE,
     * null, chatPartners.toArray(), chatPartners.iterator().next() // Default
     * to the first partner );
     *
     * if (selectedPartner != null) { openChatDialog(username, selectedPartner);
     * } else { JOptionPane.showMessageDialog(null, "No chat selected."); }
    }
     */
    public static void showRecentMessages(String username) {
        ArrayList<Message> allMessages = loadMessagesFromJsonFile();

        // Set of unique users the logged-in user has interacted with
        Set<String> chatPartners = new HashSet<>();

        for (Message msg : allMessages) {
            if (msg.getSender().equals(username)) {
                chatPartners.add(msg.getRecipient());
            }
            if (msg.getRecipient().equals(username)) {
                chatPartners.add(msg.getSender());
            }
        }

        // ✅ If no recent messages, show a message and return
        if (chatPartners.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You have no recent chats.");
            return;
        }

        // Create a list of chat partners with clickable names
        String selectedPartner = (String) JOptionPane.showInputDialog(
                null,
                "Recent Chats:",
                "Select Chat Partner",
                JOptionPane.PLAIN_MESSAGE,
                null,
                chatPartners.toArray(),
                chatPartners.iterator().next() // Default selection
        );

        if (selectedPartner != null) {
            openChatDialog(username, selectedPartner);
        } else {
            JOptionPane.showMessageDialog(null, "No chat selected.");
        }
    }

    private static void openChatDialog(String username, String partner) {
        // Filter messages between the logged-in user and the selected chat partner
        ArrayList<Message> allMessages = loadMessagesFromJsonFile();
        StringBuilder conversation = new StringBuilder();

        for (Message msg : allMessages) {
            boolean isRelevant = (msg.getSender().equals(username) && msg.getRecipient().equals(partner))
                    || (msg.getSender().equals(partner) && msg.getRecipient().equals(username));

            if (isRelevant) {
                // Mark as read if user is recipient
                if (msg.getRecipient().equals(username)) {
                    msg.setRead(true);
                }

                // Build message
                conversation.append(msg.getSender())
                        .append(": ")
                        .append(msg.getContent());

                // Only show read receipt if user is sender
                if (msg.getSender().equals(username)) {
                    conversation.append(msg.isRead() ? " (✓ Read)" : " (✓ Unread)");
                }

                conversation.append("\n");
            }
        }

        // Save updated read statuses
        saveMessagesToJsonFile(allMessages);

        // Create a JTextArea to show the conversation (read-only)
        JTextArea conversationArea = new JTextArea(conversation.toString());
        conversationArea.setEditable(false);
        conversationArea.setWrapStyleWord(true);
        conversationArea.setLineWrap(true);
        conversationArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Increase font size
        conversationArea.setBackground(new Color(240, 240, 240)); // Light background for better readability

        // Add a scroll bar to make it easier to scroll through messages
        JScrollPane conversationScrollPane = new JScrollPane(conversationArea);
        conversationScrollPane.setPreferredSize(new Dimension(300, 300)); // Adjust scroll panel size

        // Create a text field to type new messages
        JTextField messageField = new JTextField(); // This is the message input field
        messageField.setPreferredSize(new Dimension(300, 30)); // Set the size of the input field

        // Create a send button
        JButton sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(80, 30)); // Adjust the size of the send button
        sendButton.addActionListener(e -> {
            String content = messageField.getText().trim();
            if (!content.isEmpty()) {
                sendMessageInChat(username, partner, content, messageField, conversationArea);
            }
        });

        // Create a panel for the conversation and input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Align the button to the right
        inputPanel.add(messageField);
        inputPanel.add(sendButton);

        // Create the main chat panel with BorderLayout
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.add(conversationScrollPane, BorderLayout.CENTER); // Add the conversation scroll pane
        chatPanel.add(inputPanel, BorderLayout.SOUTH); // Add the input panel at the bottom

        // Create the main dialog box for the chat interface
        JOptionPane.showOptionDialog(null, chatPanel, "Chat with " + partner,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
    }

    private static void sendMessageInChat(String username, String partner, String content,
            JTextField messageField, JTextArea conversationArea) {
        if (content == null || content.trim().isEmpty()) {
            return; // Do nothing if the content is empty
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String replyToMessageId = null;
        Message message = new Message(username, partner, content.trim(), timestamp, replyToMessageId);

        // Load existing messages
        ArrayList<Message> messageList = loadMessagesFromJsonFile();

        // Add the new message to the list
        messageList.add(message);

        // Save the updated list of messages back to the JSON file
        saveMessagesToJsonFile(messageList);

        // Append to the conversation area (UI)
        conversationArea.append(username + ": " + content.trim() + "\n");
        conversationArea.setCaretPosition(conversationArea.getDocument().getLength());

        // Clear the input field
        messageField.setText("");
    }

    // Load messages from the text file
    static void loadMessages() {
        messages.clear(); // Clear any existing messages in memory before loading from file

        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messages.add(line); // Add each line (message) to the list
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading messages from file.");
            e.printStackTrace();
        }
    }

}
