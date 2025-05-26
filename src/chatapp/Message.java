package chatapp;

public class Message {
    private String sender;
    private String recipient;
    private String content;
    private String timestamp;
    private boolean read;
    private String replyToMessageId; // To store the ID of the message being replied to

    // Constructor
    public Message(String sender, String recipient, String content, String timestamp, String replyToMessageId) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
        this.read = false;  // Initially mark the message as unread
        this.replyToMessageId = replyToMessageId; // Set the reply message ID
    }

    // Getters and Setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(String replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    // Method to check if the message involves a specific user (either as sender or recipient)
    public boolean involvesUser(String username) {
        return sender.equals(username) || recipient.equals(username);
    }
    
}
