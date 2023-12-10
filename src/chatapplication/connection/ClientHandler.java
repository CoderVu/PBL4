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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public void writeMessage(String type, String fromUsername, String toUsername, String messageText, String messageIcon) throws IOException {
        bw.write(type + " " + fromUsername + " " + toUsername + " " + messageText + " " + messageIcon + "\r\n");
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

    public void writeMessage(String type, String fromUsername, String message, String messageIcon, ArrayList<String> users) throws IOException {
        String listUsername = "[";
        for (String username : users) {
            System.out.println("ROOM: " + username);
            listUsername = listUsername.concat(username + "/");

        }
        listUsername = listUsername.concat("]");
        bw.write(type + " " + fromUsername + " " + listUsername + " " + message + " " + messageIcon + "\r\n");
        bw.flush();
    }
    
    public String makeImage(String imageName, String imagelink) throws IOException{
        Path sourceImagePath = Path.of(imagelink);

            // Đường dẫn đến ảnh mới
        System.out.println(sourceImagePath +"\n");
            
        String name = "receive" + imageName;
        
        name = name.replace(" ", "");
//        if (name.contains(".jpg")||name.contains(".JPG")){
//            name = name.replace(".jpg", ".png");
//        }
        String pathNew = "C:\\PBL4\\ChatApplication\\src\\chatapplication\\serverPicture\\" + name;
        Path destinationImagePath = Path.of(pathNew);
        System.out.println(destinationImagePath + "\n");

        // Copy ảnh từ máy tính vào thư mục làm việc
        Path copiedImagePath = Files.copy(sourceImagePath, destinationImagePath, StandardCopyOption.REPLACE_EXISTING);
        
        return pathNew;
    }

    public void sendFile(ArrayList<String> filesToSend) {
        try {
            bw.write("file_transfer\r\n");
            bw.flush();
            String fileData = "";
            for (String file : filesToSend) {
                fileData += file;
//                bw.write(file + "\r\n");
//                bw.flush();

                try (FileInputStream fis = new FileInputStream(new File(file))) {
                    OutputStream os = clientSocket.getOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                        os.flush();
                    }
                }
            }
            bw.write(fileData + "\r\n");
            bw.flush();
            System.out.println("Sent " + filesToSend.size() + " files to server.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void receiveFiles() {
        try {
            // Receive file transfer command from server
            String command = br.readLine();
            if ("file_transfer".equals(command)) {
                // Receive each file from the server
                while (true) {
                    String fileName = br.readLine();
                    if (fileName == null || fileName.isEmpty()) {
                        break;
                    }

                    try (FileOutputStream fos = new FileOutputStream("ReceivedFiles\\" + fileName)) {
                        while (true) {
                            String data = br.readLine();
                            if (data == null || data.isEmpty()) {
                                break;
                            }

                            byte[] buffer = data.getBytes();
                            fos.write(buffer, 0, buffer.length);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
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
                        StringBuilder builder = new StringBuilder();
                        StringBuilder builder1 = new StringBuilder();
                        if (!data[data.length - 1].contains("/chatapplication/") && !data[data.length - 1].contains("\\chatapplication\\") ) {
                            for (int i = 3; i < data.length; i++) {
                                builder.append(data[i]).append(" ");
                            }
                            builder1.append(" ");
                        } else {
                            builder.append(" ");
                            builder1.append(data[data.length - 1] + " ");
                            System.out.println(builder1.toString());
                        }
                        
                        if (data[0].equalsIgnoreCase("chat")) {
                            chat = client.getChat();
                            String fromUser = data[1];
                            String toUser = data[2];
                            chat.sendMessage(fromUser.trim(), toUser.trim(), builder.toString(), builder1.toString(), "chat");
                            System.out.println("test1 " + data[1] + " " + data[2] + ": " + builder.toString());
                        }
                        if (data[0].equalsIgnoreCase("chatimage")) {
                            chat = client.getChat();
                            String fromUser = data[1];
                            String toUser = data[2];
                            chat.sendMessage(fromUser.trim(), toUser.trim(), builder.toString(), builder1.toString(), "chatimage");
                            System.out.println("test1 " + data[1] + " " + data[2] + ": " + builder.toString());
                        }
                        if (data[0].equalsIgnoreCase("room")) {
                            System.out.println("YES");
                            rooms = client.getRooms();
                            String fromUser = data[1];
                            String toUser = data[2];
                            rooms.receiveMessage(fromUser.trim(), toUser.trim(), builder.toString(),builder1.toString());
                            System.out.println("test2 " + data[1] + " " + data[2] + ": " + builder.toString() + builder1.toString());
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
