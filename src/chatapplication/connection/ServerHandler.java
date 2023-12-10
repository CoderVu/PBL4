
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

/**
 *
 * @author root
 */
public class ServerHandler extends Thread {

    private BufferedReader br;
    private BufferedWriter bw;
    private Server server;
    private String receivedMessage = "";
    private InputStream in;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public ServerHandler(Server server, BufferedReader br, BufferedWriter bw, InputStream in) {
        this.server = server;
        this.br = br;
        this.bw = bw;
        this.in = in;
    }

    @Override
    public void run() {
        while (true) {
            try {
                receivedMessage = br.readLine();
                System.out.println("received:  " + receivedMessage);
                if (receivedMessage != null && !receivedMessage.isEmpty()) {
                    //Thêm thời gian chat vào tin nhắn
                    server.sendToAllClients(receivedMessage);
                }
                
            } catch (IOException ex) {
                disconnect();
            }
        }
    }

    public void send() throws IOException {
        bw.write(receivedMessage);
        bw.flush();
    }

    public String getMessage() {
        return receivedMessage;
    }

    public void disconnect() {
        server.getClients().remove(this);
    }

}
