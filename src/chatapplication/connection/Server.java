package chatapplication.connection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Server {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ArrayList<ServerHandler> clients;
    private ArrayList<BufferedWriter> writer;
    private static Server server;
    private InputStream in;

    private static final int IMAGE_PORT = 12344;
    private static final String IMAGE_SAVE_PATH = "C:\\PBL4\\ChatApplication-Copy\\src\\chatapplication\\serverPicture\\%s";
    private static final ArrayList<Socket> connectedImageClients = new ArrayList<>();

    public static void main(String[] args) {
        server = new Server();
        startImageServer();
    }

    public Server() {
        try {
            this.serverSocket = new ServerSocket(12345);
            this.clients = new ArrayList<>();
            this.writer = new ArrayList<>();
            this.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        Thread listenThread = new Thread(() -> {
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    in = clientSocket.getInputStream();
                    ServerHandler client = new ServerHandler(server, new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream())),
                            new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), in);
                    clients.add(client);
                    writer.add(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                    Thread clientThread = new Thread(client);
                    clientThread.start();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        listenThread.start();
    }

    public void sendToAllClients(String message) {
        for (BufferedWriter bw : writer) {
            try {
                bw.write(message + "\r\n");
                bw.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void tryToReconnect() throws IOException {
        this.serverSocket.close();
        clientSocket = null;
        System.gc();
        clientSocket = serverSocket.accept();
    }

    public ArrayList<ServerHandler> getClients() {
        return this.clients;
    }

    private static void startImageServer() {
        try (ServerSocket imageServerSocket = new ServerSocket(IMAGE_PORT)) {
        //
            //     System.out.println("Image server is listening on port " + IMAGE_PORT);

            while (true) {
                Socket imageClientSocket = imageServerSocket.accept();
                connectedImageClients.add(imageClientSocket);
                handleImageClient(imageClientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleImageClient(Socket imageClientSocket) {
        try (InputStream inputStream = imageClientSocket.getInputStream()) {
            // Read the original file name
            StringBuilder fileNameBuilder = new StringBuilder();
            int byteRead;
            while ((byteRead = inputStream.read()) != 0) {
                fileNameBuilder.append((char) byteRead);
            }
            String originalFileName = fileNameBuilder.toString();

            // Read the image data
            byte[] imageData = inputStream.readAllBytes();

            // Broadcast the image to all clients
            broadcastImageToClients(originalFileName, imageData);

            // Save the image to the client's folder
            saveImageToClient(originalFileName, imageData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void broadcastImageToClients(String originalFileName, byte[] imageData) {
        ArrayList<Socket> disconnectedClients = new ArrayList<>();

        for (Socket clientSocket : connectedImageClients) {
            try (OutputStream outputStream = clientSocket.getOutputStream()) {
                // Check if the socket is still connected
                if (!clientSocket.isConnected() || clientSocket.isClosed()) {
                    disconnectedClients.add(clientSocket);
                    continue;
                }

                // Send the original file name to the client
                outputStream.write(originalFileName.getBytes());
                outputStream.write('\0');
                outputStream.flush();
                // Send the image data to the client
                outputStream.write(imageData);
                outputStream.flush();
            } catch (IOException e) {
                // Handle IOException (e.g., log the error)
                disconnectedClients.add(clientSocket);
            }
        }
        connectedImageClients.removeAll(disconnectedClients);
    }

    private static void saveImageToClient(String originalFileName, byte[] imageData) {
        String newFileName = "receive" + originalFileName;
        String savePath = String.format(IMAGE_SAVE_PATH, newFileName);

        try {
            // Save the image to the client's folder
            Path imagePath = Path.of(savePath);
            Files.write(imagePath, imageData);
            System.out.println("Image saved to client: " + savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
