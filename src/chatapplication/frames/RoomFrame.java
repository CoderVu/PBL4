/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication.frames;

import chatapplication.connection.ClientHandler;
import chatapplication.database_connection.DatabaseManager;
import chatapplication.emoji.Emogi;
import chatapplication.emoji.Model_Emoji;
import chatapplication.event.EventImageView;
import chatapplication.event.PublicEvent;
import chatapplication.main.Client;
import chatapplication.rooms.Room;
import chatapplication.rooms.RoomManager;
import com.mysql.jdbc.PreparedStatement;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author root
 */
public class RoomFrame extends javax.swing.JInternalFrame {

    private Room room;
    private DatabaseManager database;
    private ClientHandler clientHandler;
    private DefaultListModel model;
    private RoomManager roomManager; // Add a reference to your RoomManager instance.
    private String lastDisplayedMessage = "";
    private AddNewRoom addroom;
    private JDesktopPane desktop;

    /**
     * Creates new form RoomFrame
     */
    public RoomFrame(Room room, DatabaseManager database, ClientHandler clientHandler, JDesktopPane desktop) {
        this.database = database;
        this.room = room;
        this.clientHandler = clientHandler;
        this.desktop = desktop;
        this.roomManager = roomManager; // Initialize roomManager
        initComponents();
        setTitle(room.getRoom());
        createUserList();
        // Assuming you have a method to get the user's ID by their username
        int currentUserId = getUserIdByUsername(database.user.getUsername());

        // Call loadChatHistory with the currentUserId
        loadChatHistory(room.getRoom(), currentUserId);

        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent evt) {
                try {
                    onExit();
                } catch (SQLException ex) {
                    Logger.getLogger(RoomFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        userChat.setLineWrap(true);
        userChat.setWrapStyleWord(true);
        JPanel panelMore = new JPanel();
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));
        panelMore.setVisible(false);
        jPanel1.add(chat);
        jPanel1.add(jPanel3);
        jPanel1.add(panelMore);
        jPanel1.setVisible(true);
        butMore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (panelMore.isVisible()) {
                    butMore.setIcon(new ImageIcon(getClass().getResource("/chatapplication/testPicture/more-25.png")));
                    panelMore.setVisible(false);
                    //panelMore.revalidate();
                    //panelMore.repaint();
                    
                } else {
                    butMore.setIcon(new ImageIcon(getClass().getResource("/chatapplication/testPicture/moreDisable-25.png")));
                    panelMore.setBackground(Color.white);
                    panelMore.setVisible(true);
                    
                    //panelMore.revalidate();
                    //panelMore.repaint();
                }
            }
        });
        addPanelMore(panelMore);
        actions();
    }
    private ArrayList<String> userList = new ArrayList<>();
    private String username;

    public void addPanelMore(JPanel p){
        //p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(Color.white);
        JPanel panelDetail = new JPanel();
        panelDetail.setLayout(new MigLayout());
        panelDetail.setBackground(Color.white);
        
        JButton cmd = new JButton();
        cmd.setIcon(new ImageIcon(getClass().getResource("/chatapplication/testPicture/link.png")));
        cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser ch = new JFileChooser();
                ch.showOpenDialog(Client.getFrames()[0]);
                //  Update next
            }
        });
        
        //button send image
        JButton cmd1 = new JButton();
        cmd1.setIcon(new ImageIcon(getClass().getResource("/chatapplication/testPicture/eyes-25.png")));
        cmd1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                cmd1.setSelected(true);
                panelDetail.removeAll();
                int i=1;
                for (Model_Emoji d : Emogi.getInstance().getStyle1()) {
                    if (i%10==0){
                        panelDetail.add(getButton(d), "wrap");
                    }else{
                        panelDetail.add(getButton(d));
                    }
                    i++;
                }
                panelDetail.repaint();
                panelDetail.revalidate();
            }
        });
        
        JButton cmd2 = new JButton();
        cmd2.setIcon(new ImageIcon(getClass().getResource("/chatapplication/testPicture/link.png")));
        cmd2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                
                cmd2.setSelected(true);
                panelDetail.removeAll();
                for (Model_Emoji d : Emogi.getInstance().getStyle1()) {
                    panelDetail.add(getButton(d));
                }
                panelDetail.repaint();
                panelDetail.revalidate();
            }
        });
        panelHeader.add(cmd);
        panelHeader.add(cmd1);
        panelHeader.add(cmd2);
        p.add(panelHeader);
        p.add(panelDetail);
    }
    
    private JButton getButton(Model_Emoji data) {
        JButton cmd = new JButton(data.getIcon());
        cmd.setName(data.getId() + "");
        cmd.setBorder(new EmptyBorder(3, 3, 3, 3));
        cmd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmd.setContentAreaFilled(false);
        cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String message = "/chatapplication/icon/" + data.getId() + ".png";
                if (!message.isEmpty() && !sendingMessage) {
            // Lấy thời gian hiện tại và định dạng nó
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentTime = dateFormat.format(new Date());

                    // Tạo nội dung tin nhắn với thời gian
                    String messageTextWithTimestamp = "[" + currentTime + "]: " + "";

                    // Thay thế database.user.getUsername() bằng senderId
                    int senderId = getUserIdByUsername(database.user.getUsername());

                    // Lưu tin nhắn vào cơ sở dữ liệu
                    saveMessageToDatabase("", message, currentTime, senderId);

                    // Set sendingMessage to true to prevent sending the message again while processing
                    sendingMessage = true;
                    try {
                        // Gửi tin nhắn cho danh sách người dùng
                        clientHandler.writeMessage("room", database.user.getUsername(), messageTextWithTimestamp, message, userList);
                    } catch (IOException ex) {
                        Logger.getLogger(RoomFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    // Reset sendingMessage to allow sending another message
                    sendingMessage = false;

                    userChat.setText(""); // Xóa nội dung trong JTextArea để chuẩn bị cho tin nhắn tiếp theo
                }
            }
        });
        return cmd;
    }
    
    public void leaveRoom() {
        if (userList.contains(username)) {
            userList.remove(username);
            createUserList();
            clientHandler.chatUserLeft("roomleave", username, room.getRoom());

            // Use the roomManager instance to leave the room.
            roomManager.userLeaveRoom(room.getRoom(), username);
        }
    }

    public void createUserList() {
        users.removeAll();
        users.setModel(createUsers(room.getRoom()));
    }

    public void actions() {
        PublicEvent.getInstance().addEventImageView(new EventImageView(){
            @Override
            public void viewImage(Icon image){
                viewImage.viewImage(image);
            }

            @Override
            public void saveImage(Icon image) {
                
            }
            
        });
        userChat.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    sendButton.doClick();
                }
            }
        });
        userChat.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scrollToBottom();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                scrollToBottom();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                scrollToBottom();
            }

            private void scrollToBottom() {
                userChat.setCaretPosition(userChat.getDocument().getLength());
            }
        });
    }

    public void userConnected(String username, String roomName) {
        if (roomName.equals(room.getRoom())) {
            if (!userList.contains(username)) {
                userList.add(username);
                createUserList();
            }
        }
    }

    public void userDisconnected(String username, String roomName) {
        if (roomName.equals(room.getRoom())) {
            userList.remove(username);
            createUserList();
        }
    }

    public DefaultListModel createUsers(String nameRoom) {
        model = new DefaultListModel();
        int idroom = 0;
        try {
            PreparedStatement id = (PreparedStatement) database.connection.prepareStatement("select username from users inner join room_users on room_users.id_user = users.id where room_users.id_room = " + room.getIdROom());
            ResultSet rs1 = id.executeQuery();
            while (rs1.next()){
                if (!rs1.getString(1).equals(database.user.getUsername())) {
                    model.addElement(rs1.getString(1));
                    userList.add(rs1.getString(1));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoomsFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return model;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
        chat = new chatapplication.component.Chat_Body();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        userChat = new javax.swing.JTextArea();
        butMore = new javax.swing.JButton();
        sendButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        users = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        changeroom_button = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        butOut = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        viewImage = new chatapplication.frames.ViewImage();

        setClosable(true);
        getContentPane().setLayout(new java.awt.CardLayout());

        userChat.setColumns(20);
        userChat.setLineWrap(true);
        userChat.setRows(5);
        jScrollPane2.setViewportView(userChat);

        butMore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chatapplication/testPicture/more-25.png"))); // NOI18N

        sendButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chatapplication/testPicture/sent-25.png"))); // NOI18N
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2)
                .addGap(0, 0, 0)
                .addComponent(butMore)
                .addGap(0, 0, 0)
                .addComponent(sendButton)
                .addGap(0, 0, 0))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton)
                    .addComponent(butMore))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(9, 9, 9))
                    .addComponent(chat, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(chat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        users.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(users);

        jLabel1.setText("Users in room");

        changeroom_button.setText("change room");
        changeroom_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                changeroom_buttonMouseClicked(evt);
            }
        });
        changeroom_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeroom_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(changeroom_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(changeroom_button)
                .addGap(0, 0, 0)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        butOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chatapplication/testPicture/icons8-leave-25.png"))); // NOI18N
        butOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOutActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chatapplication/testPicture/icons8-add-male-user-25.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(butOut)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 68, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(butOut)))
        );

        jLayeredPane1.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jPanel2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jPanel4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getContentPane().add(jLayeredPane1, "card2");

        viewImage.setToolTipText("");
        getContentPane().add(viewImage, "card3");

        pack();
    }// </editor-fold>//GEN-END:initComponents
   private boolean sendingMessage = false;  // Add this variable
    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        String message = userChat.getText();

        if (!message.isEmpty() && !sendingMessage) {
            // Lấy thời gian hiện tại và định dạng nó
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = dateFormat.format(new Date());

            // Tạo nội dung tin nhắn với thời gian
            String messageTextWithTimestamp = "[" + currentTime + "]: " + message + "\n";

            // Thay thế database.user.getUsername() bằng senderId
            int senderId = getUserIdByUsername(database.user.getUsername());

            // Lưu tin nhắn vào cơ sở dữ liệu
            saveMessageToDatabase(message, "", currentTime, senderId);

            // Set sendingMessage to true to prevent sending the message again while processing
            sendingMessage = true;
            try {
                // Gửi tin nhắn cho danh sách người dùng
                clientHandler.writeMessage("room", database.user.getUsername(), message, "", userList);
            } catch (IOException ex) {
                Logger.getLogger(RoomFrame.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Reset sendingMessage to allow sending another message
            sendingMessage = false;

            userChat.setText(""); // Xóa nội dung trong JTextArea để chuẩn bị cho tin nhắn tiếp theo
        }
    }                                          
//GEN-LAST:event_sendButtonActionPerformed
    private void changeroom_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeroom_buttonActionPerformed

    }//GEN-LAST:event_changeroom_buttonActionPerformed
    private int getUserIdByUsername(String username) {
        int userId = -1; // Giá trị mặc định nếu không tìm thấy
        try {
            String query = "SELECT id FROM users WHERE username = ?";
            java.sql.PreparedStatement preparedStatement = database.connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userId = resultSet.getInt("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userId;
    }

    public int getRoomIdByRoomName(String roomName) {
        int roomId = -1; // Giá trị mặc định nếu không tìm thấy
        try {
            String query = "SELECT id FROM rooms WHERE room_name = ?";
            java.sql.PreparedStatement preparedStatement = database.connection.prepareStatement(query);
            preparedStatement.setString(1, roomName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                roomId = resultSet.getInt("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return roomId;
    }

    private String getUserNameById(int userId) {
        String userName = "";
        try {
            String query = "SELECT username FROM users WHERE id = ?";
            java.sql.PreparedStatement preparedStatement = database.connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                userName = resultSet.getString("username");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userName;
    }

    private void saveMessageToDatabase(String message, String messageIcon, String timestamp, int senderId) {
        try {
            int roomId = getRoomIdByRoomName(room.getRoom()); // Lấy room_id dựa trên room_name
            String insertquery = "INSERT INTO chat_messages (sender_id, receiver_id, message_text, timestamp, message_icon, chat_room_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = (PreparedStatement) database.connection.prepareStatement(insertquery);
            preparedStatement.setInt(1, senderId); // ID của người gửi (senderId thay vì database.user.getUsername())
            preparedStatement.setInt(2, roomId); // Thay vì sử dụng room.getRoom(), sử dụng room_id
            preparedStatement.setString(3, message); // Nội dung tin nhắn
            preparedStatement.setString(4, timestamp); // Thời gian gửi tin nhắn
            preparedStatement.setString(5, messageIcon); 
            preparedStatement.setInt(6, roomId); // Thay vì sử dụng room.getRoom(), sử dụng room_id
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void loadChatHistory(String roomName, int currentUserId) {
        try {
            // Xây dựng truy vấn SQL để lấy lịch sử tin nhắn dựa trên room_name
            String query = "SELECT sender_id, message_text, timestamp, message_icon FROM chat_messages "
                    + "WHERE chat_room_id = (SELECT id FROM rooms WHERE room_name = ?) "
                    + "ORDER BY timestamp";
            java.sql.PreparedStatement preparedStatement = database.connection.prepareStatement(query);
            preparedStatement.setString(1, roomName);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Xóa nội dung cửa sổ chat hiện tại
            chat.clear();

            // Hiển thị lịch sử tin nhắn trong cửa sổ chat
            while (resultSet.next()) {
                int messageSenderId = resultSet.getInt("sender_id");
                String messageText = resultSet.getString("message_text");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                String messageIcon = resultSet.getString("message_icon");
                
                String senderName = getUserNameById(messageSenderId);

                String messageWithTimestamp = "[" + timestamp + "] " + senderName + ": " + messageText;

                // Determine whether the message was sent by the current user or received from others
                if (messageSenderId == currentUserId) {
                    if (messageIcon==null){
                        chat.addItemRight(messageWithTimestamp);
                    }else{
                        chat.addItemRight(messageWithTimestamp, new ImageIcon(getClass().getResource(messageIcon)));
                    }
                } else {
                    if (messageIcon==null){
                        chat.addItemLeft(messageWithTimestamp);
                    }else{
                        chat.addItemLeft(messageWithTimestamp, new ImageIcon(getClass().getResource(messageIcon)));
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void changeroom_buttonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeroom_buttonMouseClicked
        this.setVisible(false);
        try {
            PreparedStatement delete = database.Delete("room_users", "user = '" + database.user.getUsername() + "'");

        } catch (SQLException ex) {
            Logger.getLogger(RoomFrame.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        leaveRoom();
    }//GEN-LAST:event_changeroom_buttonMouseClicked

    private void butOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOutActionPerformed
        int result = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn rời khỏi phòng không?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION){
            try {
                String sqlDel = "delete from room_users where (id_user = " + database.user.getId() + " and id_room = " + room.getIdROom() + ")";
                PreparedStatement delete = (PreparedStatement) database.getConnection().prepareStatement(sqlDel);
                int rowsAffected = delete.executeUpdate();
                if (rowsAffected > 0) {
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                Logger.getLogger(RoomFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    
    }//GEN-LAST:event_butOutActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String newUsername = JOptionPane.showInputDialog(this, "Enter the username to add to the room:");

        // Check if the entered username is valid
        if (newUsername != null && !newUsername.isEmpty()) {
            try {
                // Get the user's ID by their username
                int newUserId = getUserIdByUsername(newUsername);

                if (newUserId != -1) { // Kiểm tra xem người dùng có tồn tại hay không
                    // Check if the user is already in the room
                    if (!userList.contains(newUsername)) {
                        // Add the user to the room in the database
                        String sqlAddUser = "INSERT INTO room_users (id_user, id_room) VALUES (?, ?)";
                        try (PreparedStatement addUser = (PreparedStatement) database.getConnection().prepareStatement(sqlAddUser)) {
                            addUser.setInt(1, newUserId);
                            addUser.setInt(2, room.getIdROom());
                            int rowsAffectedUser = addUser.executeUpdate();

                            if (rowsAffectedUser > 0) {
                                // Update the user list in the UI
                                userConnected(newUsername, room.getRoom());

                                // Notify all users in the room about the new user
                                clientHandler.chatUserJoined("roomjoin", newUsername, room.getRoom());
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed to add user to the room. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "User is already in the room.", "Information", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User does not exist.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                Logger.getLogger(RoomFrame.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error accessing the database. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    
    public void receiveMessage(String fromUser, String toUser, String message, String messageIcon) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateFormat.format(new Date());

        if (fromUser.equalsIgnoreCase(database.user.getUsername())) {
            // Display the message sent by the current user
            if (!messageIcon.isEmpty()){
                chat.addItemRight("[" + currentTime + "] " + fromUser + ": " + message, new ImageIcon(getClass().getResource(messageIcon)));
            }else{
                chat.addItemRight("[" + currentTime + "] " + fromUser + ": " + message);
            }

            System.out.println("Gửi:" + message + messageIcon);
        } else {
            // Display messages from others
            if (!messageIcon.isEmpty()){
                chat.addItemLeft("[" + currentTime + "] " + fromUser + ": " + message, new ImageIcon(getClass().getResource(messageIcon)));
            }else{
                chat.addItemLeft("[" + currentTime + "] " + fromUser + ": " + message);
            }

            System.out.println("Nhận:" + message + messageIcon);
        }
            //lastDisplayedMessage = message;
    }

    private void onExit() throws SQLException {
//        PreparedStatement delete = database.Delete("room_users", "user = '" + database.user.getUsername() + "'");
//        client.chatUserLeft("roomremove", database.user.getUsername(), room.getRoom());
//        System.out.println(delete.executeUpdate());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butMore;
    private javax.swing.JButton butOut;
    private javax.swing.JButton changeroom_button;
    private chatapplication.component.Chat_Body chat;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton sendButton;
    private javax.swing.JTextArea userChat;
    private javax.swing.JList<String> users;
    private chatapplication.frames.ViewImage viewImage;
    // End of variables declaration//GEN-END:variables
}
