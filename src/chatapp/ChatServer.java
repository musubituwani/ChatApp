package chatapp;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    static Map<String, PrintWriter> clients = new HashMap<>();         // username → writer
    static Map<String, String> messageSenders = new HashMap<>();       // msgId → sender

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Server running...");

        while (true) {
            Socket socket = serverSocket.accept();
            new ClientHandler(socket).start();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                username = in.readLine(); // Receive authenticated username
                if (username == null || username.trim().isEmpty()) return;

                synchronized (clients) {
                    clients.put(username, out);
                }

                System.out.println(username + " connected.");

                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.startsWith("TO:")) {
                        String[] parts = msg.split("\\|");
                        if (parts.length < 3) continue;

                        String toUser = parts[0].substring(3).trim();
                        String msgId = parts[1].substring(3).trim();
                        String content = parts[2].substring(4).trim();

                        messageSenders.put(msgId, username); // Save who sent this message

                        PrintWriter recipientOut = clients.get(toUser);
                        if (recipientOut != null) {
                            recipientOut.println("FROM:" + username + "|ID:" + msgId + "|MSG:" + content);
                            recipientOut.flush();

                            out.println("STATUS:" + msgId + "|DELIVERED");
                            out.flush();
                        } else {
                            out.println("STATUS:" + msgId + "|NOT_DELIVERED");
                            out.flush();
                        }

                    } else if (msg.startsWith("STATUS:")) {
                        String[] parts = msg.split("\\|");
                        if (parts.length < 2) continue;

                        String msgId = parts[0].substring(7).trim();
                        String status = parts[1].trim();

                        String originalSender = messageSenders.get(msgId);
                        if (originalSender != null && clients.containsKey(originalSender)) {
                            PrintWriter senderOut = clients.get(originalSender);
                            senderOut.println("STATUS:" + msgId + "|" + status);
                            senderOut.flush();
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection lost: " + username);
            } finally {
                try {
                    socket.close();
                    synchronized (clients) {
                        clients.remove(username);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
