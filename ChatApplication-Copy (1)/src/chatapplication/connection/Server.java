package chatapplication.connection;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Server extends JFrame {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ArrayList<ServerHandler> clients;
    private ArrayList<BufferedWriter> writer;
    private static Server server;
    private InputStream in;

    private static final int IMAGE_PORT = 12344;
    private static final String IMAGE_SAVE_PATH = "C:\\PBL4\\ChatApplication-Copy (1)\\src\\chatapplication\\serverPicture\\%s";
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

            // Set up the JFrame
            setTitle("Server");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1050, 700);
            // Tạo một JLabel với nội dung "Server CHAT"
            JLabel label = new JLabel("Server CHAT");
            label.setFont(new Font("Arial", Font.BOLD, 20));
            label.setBounds(410, 10, 200, 30);

            // Create a JTextArea to display server logs
            JTextArea logTextArea = new JTextArea();
            logTextArea.setBackground(Color.CYAN);
            logTextArea.setForeground(Color.BLACK);
            logTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
            logTextArea.setEditable(false);
            // Add the JTextArea to a JScrollPane
            JScrollPane scrollPane = new JScrollPane(logTextArea);
            scrollPane.setBounds(10, 40, 1000, 600);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            // Add the JLabel to the  JFrame
            getContentPane().add(label);
            // Add the JScrollPane to the JFrame
            getContentPane().add(scrollPane);
            // Set the layout manager to null to use absolute positioning
            setLayout(null);

            // Display the JFrame
            setVisible(true);

            // Redirect System.out to the JTextArea
            PrintStream printStream = new PrintStream(new CustomOutputStream(logTextArea));
            System.setOut(printStream);
            System.setErr(printStream);

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
                    // Handle the IOException (e.g., log the error)
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

    // CustomOutputStream class to redirect System.out to a JTextArea
    private static class CustomOutputStream extends OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}