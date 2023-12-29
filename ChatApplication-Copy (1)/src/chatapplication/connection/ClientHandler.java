package chatapplication.connection;

import chatapplication.frames.ChatFrame;
import chatapplication.frames.RoomFrame;
import chatapplication.main.Client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ClientHandler {

    private BufferedWriter bw;
    private BufferedReader br;
    private Client client;
    private ChatFrame chat;
    private RoomFrame rooms;
    private Socket clientSocket;
    private ClientListener listener;
    private OutputStream out;
    private InputStream in;
    private String messageImage = "";
    private byte[] dataImage = new byte[100000];
    private byte[] newArray = new byte[100000];
    private static SecretKey secretKey;
    
   

    public ClientHandler(Client client) {
        this.client = client;
        chat = client.getChat();
        try {
            clientSocket = new Socket("192.168.2.22", 12345);
            out = clientSocket.getOutputStream();
            in = clientSocket.getInputStream();
            bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            bw.write("user " + chat.getDatabase().user.getUsername() + "\r\n");
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public interface ClientListener {

        void onFileReceived(String message);
    }

    public void userAdded(String type, String fromUser, String room) throws Exception {
        System.out.println(room);
        bw.write(type + " " + fromUser + " " + room + "\r\n");
        bw.flush();
    }

    public void chatUserJoined(String messageType, String username, String roomName) {
        if ("roomjoin".equals(messageType)) {
            // Logic to handle a user joining a room
            System.out.println(username + " has joined the room: " + roomName);
            // You can update your user list or take other actions as needed
        }
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02x", b));
        }
        return hexStringBuilder.toString();
    }
    
    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                                 + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
    

    public void writeMessage(String type, String fromUsername, String toUsername, String messageText, String messageIcon) throws IOException, Exception {
        secretKey = generateSecretKey();
        String hex = bytesToHex(secretKey.getEncoded());
        String encryptedMessage = encrypt(messageText, secretKey);
        String encryptedImage = encrypt(messageIcon, secretKey);
        bw.write(type + " " + fromUsername + " " + toUsername + " " + hex + " " + encryptedMessage + " " + encryptedImage + "\r\n");
        bw.flush();
    }
    
    public void writeMessageImage(String type, String fromUsername, String toUsername, String messageText, String messageName, String messageImage) throws IOException {
        //bw.write(type + " " + fromUsername + " " + toUsername + " " + messageImge + "\r\n");
        //sendImage(messageImage);
//        bw.write("SendImage\r\n");
//        bw.flush();
            //bw.write("\nchatendImage\r\n");  
            //bw.flush();
        //String path = makeImage(messageName, messageImage);
        //System.out.println(path);
        bw.write(type + " " + fromUsername + " " + toUsername + " " + messageText + " " + messageImage + "\r\n");
        bw.flush();
        
    }

    public void userLeft(String type, String user) {
        try {
            bw.write(type + " " + user + "\r\n");
            bw.flush();
        } catch (Exception ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void chatUserLeft(String type, String user, String room) {
        try {
            bw.write(type + " " + user + " " + room + "\r\n");
            bw.flush();
        } catch (Exception ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeMessage(String type, String fromUsername, String message, String messageIcon, ArrayList<String> users) throws IOException, Exception {
        String listUsername = "[";
        for (String username : users) {
            System.out.println("ROOM: " + username);
            listUsername = listUsername.concat(username + "/");

        }
        listUsername = listUsername.concat("]");
        secretKey = generateSecretKey();
        String hex = bytesToHex(secretKey.getEncoded());
        String encryptedMessage = encrypt(message, secretKey);
        String encryptedImage = encrypt(messageIcon, secretKey);
        bw.write(type + " " + fromUsername + " " + listUsername + " " + hex + " " + encryptedMessage + " " + encryptedImage + "\r\n");
        bw.flush();
    }

    private static SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // Kích thước khóa 128-bit
        return keyGenerator.generateKey();
    }

    // Mã hóa tin nhắn sử dụng AES
    private static String encrypt(String plaintext, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decrypt(String ciphertext, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(hexStringToByteArray(ciphertext));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void newReading() {
        Thread readingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        
                        String message = br.readLine();
                        String[] data = message.split(" ");
                        String decryptedMessage = "";
                        String decryptedImage = "";
                        if (data[0].equalsIgnoreCase("chat")) {
                            try {
                                byte[] keyBytes = hexStringToByteArray(data[3].trim());
                                
                                decryptedMessage = decrypt(data[4], new SecretKeySpec(keyBytes, "AES"));
                                decryptedImage = decrypt(data[5], new SecretKeySpec(keyBytes, "AES"));
                            } catch (Exception ex) {
                                Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            chat = client.getChat();
                            String fromUser = data[1];
                            String toUser = data[2];
                            chat.sendMessage(fromUser.trim(), toUser.trim(), decryptedMessage, decryptedImage, "chat");
                            System.out.println("test1 " + data[1] + " " + data[2] + ": " + decryptedMessage);
                        }
                        
                        if (data[0].equalsIgnoreCase("chatimage")) {
                            try {
                                byte[] keyBytes = hexStringToByteArray(data[3].trim());
                                decryptedMessage = decrypt(data[4], new SecretKeySpec(keyBytes, "AES"));
                                decryptedImage = decrypt(data[5], new SecretKeySpec(keyBytes, "AES"));
                            } catch (Exception ex) {
                                Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println(decryptedImage);
                            chat = client.getChat();
                            String fromUser = data[1];
                            String toUser = data[2];
                            chat.sendMessage(fromUser.trim(), toUser.trim(), decryptedMessage, decryptedImage, "chatimage");
                            System.out.println("test1 " + data[1] + " " + data[2] + ": " + decryptedImage);
                        }
                        
                        if (data[0].equalsIgnoreCase("room")) {
                            try {
                                byte[] keyBytes = hexStringToByteArray(data[3].trim());
                                
                                decryptedMessage = decrypt(data[4], new SecretKeySpec(keyBytes, "AES"));
                                decryptedImage = decrypt(data[5], new SecretKeySpec(keyBytes, "AES"));
                            } catch (Exception ex) {
                                Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println("YES");
                            rooms = client.getRooms();
                            String fromUser = data[1];
                            String toUser = data[2];
                            rooms.receiveMessage(fromUser.trim(), toUser.trim(), decryptedMessage,decryptedImage, "chat");
                            System.out.println("test2 " + data[1] + " " + data[2] + ": " + decryptedMessage);
                        }
                        
                        if (data[0].equalsIgnoreCase("roomimage")) {
                            try {
                                byte[] keyBytes = hexStringToByteArray(data[3].trim());
                                
                                decryptedMessage = decrypt(data[4], new SecretKeySpec(keyBytes, "AES"));
                                decryptedImage = decrypt(data[5], new SecretKeySpec(keyBytes, "AES"));
                            } catch (Exception ex) {
                                Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            System.out.println("YES");
                            rooms = client.getRooms();
                            String fromUser = data[1];
                            String toUser = data[2];
                            rooms.receiveMessage(fromUser.trim(), toUser.trim(), decryptedMessage,decryptedImage, "image");
                            System.out.println("test2 " + data[1] + " " + data[2] + ": " + decryptedImage);
                        }
                        
                        if (data[0].equalsIgnoreCase("user")) {
                            System.out.println("1 " + message);
                            if (!data[1].equalsIgnoreCase(chat.getDatabase().user.getUsername())) {
                                System.out.println(data[1] + chat.getDatabase().user.getUsername());
                                chat.userConnected(data[1]);
                            }
                        }
                        if (data[0].equalsIgnoreCase("roomadd")) {
                            client.getRooms().userConnected(data[1], data[2] + " " + data[3]);
                        }
                        if (data[0].equalsIgnoreCase("roomremove")) {
                            client.getRooms().userDisconnected(data[1], data[2] + " " + data[3]);
                        }
                        if (data[0].equalsIgnoreCase("chatleft")) {
                            chat.userDisconnected(data[1]);
                        }
                        
                    } catch (IOException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });
        readingThread.start();
    }

    public void disconnect() {
        try {
            bw.close();
            br.close();
            clientSocket.close();
            clientSocket = null;
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
