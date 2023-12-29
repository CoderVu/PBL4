package chatapplication.connection;

import chatapplication.frames.ChatFrame;
import chatapplication.frames.RoomFrame;
import chatapplication.main.Client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;

public class ClientImage {

    private BufferedWriter bw;
    private BufferedReader br;
    private Client client;
    private ChatFrame chat;
    private RoomFrame rooms;
    private Socket clientSocket;
    private OutputStream out;
    private InputStream in;




    public ClientImage(Client client) {
        this.client = client;
        chat = client.getChat();
    }

    public void sendImageToServer (File selectedFile) throws IOException {
        try (Socket socket = new Socket("192.168.2.22", 12344)) {
            // Send the original filename to the server
            String originalFileName = selectedFile.getName();
            byte[] fileNameData = originalFileName.getBytes();
            OutputStream fileNameStream = socket.getOutputStream();
            fileNameStream.write(fileNameData);
            fileNameStream.write('\0');
            fileNameStream.flush();
            // Send the image data to the server
            byte[] imageData = Files.readAllBytes(selectedFile.toPath());
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(imageData);
            outputStream.flush();
            System.out.println("Image sent successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}