package chatapplication.connection;

import chatapplication.database_connection.DatabaseManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author root
 */
public class ManageUser extends Thread {

    private int userId;
    private String username;
    private DataInputStream is;
    private DataOutputStream os;

    public ManageUser(Socket client, DatabaseManager database) throws IOException {

        is = new DataInputStream(client.getInputStream());
        os = new DataOutputStream(client.getOutputStream());
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void run() {
        // Bạn có thể sử dụng database ở đây để truy vấn ID của người dùng dựa trên tên
        String username = getUsername(); // Lấy tên người dùng từ đối tượng ManageUser

    }

}
