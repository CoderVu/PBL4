    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication.frames;

import chatapplication.ChatManager.ChatHandler;
import chatapplication.ChatManager.ChatManager;
import chatapplication.database_connection.DatabaseManager;
import chatapplication.emoji.Emogi;
import chatapplication.emoji.Model_Emoji;
import chatapplication.event.EventImageView;
import chatapplication.event.PublicEvent;
import chatapplication.main.Client;
import com.mysql.jdbc.PreparedStatement;
import java.awt.Color;
import java.awt.Cursor;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Adminn
 */
public final class ChatFrame extends javax.swing.JInternalFrame {

    private DefaultListModel friends;
    private DatabaseManager database;
    private ChatManager chatManager;
    private int idClicked = 0, counter = 0;
    private String user;
    private String username;
    private JMenuItem menuProfile;
    private Client client;
    private String currentFriend = null;
    private Map<String, JTextArea> temporaryChatWindows = new HashMap<>();
    Map<String, StringBuilder> chatMessages = new HashMap<>();
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem menuItem = new JMenuItem();
    public ChatFrame(Client client, String username, DatabaseManager database) {
        this.username = username;
        this.database = database;
        this.client = client;
        initComponents();
        viewImage.setVisible(false);
        chatfr.setVisible(true);
        menuProfile = new JMenuItem("profile");
        jPopupMenu1.add(menuProfile);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        try {
            createChatList();
        } catch (SQLException ex) {
            Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        JPanel panelMore = new JPanel();
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));
        panelMore.setVisible(false);
        jPanel1.add(chat);
        jPanel1.add(jPanel2);
        jPanel1.add(panelMore);
        jPanel1.setVisible(true);
        butMore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (panelMore.isVisible()) {
                    butMore.setIcon(new ImageIcon(getClass().getResource("/chatapplication/testPicture/more-25.png")));
                    panelMore.setVisible(false);

                } else {
                    butMore.setIcon(new ImageIcon(getClass().getResource("/chatapplication/testPicture/moreDisable-25.png")));
                    panelMore.setBackground(Color.white);
                    panelMore.setVisible(true);
                }
            }
        });
        addPanelMore(panelMore);
        actions();
    }
    public void addPanelMore(JPanel p) {
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
                handleSelectButton();
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
                int i = 1;
                for (Model_Emoji d : Emogi.getInstance().getStyle1()) {
                    if (i % 10 == 0) {
                        panelDetail.add(getButton(d), "wrap");
                    } else {
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
    private void handleSelectButton() {
        File selectedFile = chooseImageFile();
        if (selectedFile != null) {
            try {
                client.getClientImage().sendImageToServer(selectedFile);

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
//
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateFormat.format(new Date());

        String formattedMessage = "[" + currentTime + "] " + database.user.getUsername() + ": " + "";
        chat.addItemRight(formattedMessage, new ImageIcon(selectedFile.getPath()));
     //  String messageImage = "/chatapplication/serverPicture/receive" + selectedFile.getName();
       String messageImage = "C:\\PBL4\\ChatApplication-Copy\\src\\chatapplication\\serverPicture\\receive" + selectedFile.getName();
       int receiverId = getUserIdByUsername(user);
        saveMessageToDatabase(database.user.getId(), receiverId, "", messageImage, currentTime);
        try {
            client.getClient().writeMessage("chatimage", database.user.getUsername(), user, "", messageImage);
        } catch (IOException ex) {
            Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private File chooseImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose an image file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            System.out.println("No file selected.");
            return null;
        }
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
                if (user != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentTime = dateFormat.format(new Date());

                    String formattedMessage = "[" + currentTime + "] " + database.user.getUsername() + ": " + "";

                    // Display the message, including the timestamp, in the sender's chat window
                    chat.addItemRight(formattedMessage, new ImageIcon(getClass().getResource("/chatapplication/icon/" + data.getId() + ".png"))); // Hiển thị tin nhắn đã gửi ngay lập tức trong cửa sổ chat của người gửi

                    // Find the chat window of the recipient (if it exists)
                    ChatHandler receiverChat = chatManager.getFriendChatByUser(user);

                    if (receiverChat != null) {
                        // Display the message in the recipient's chat window
                        String messageWithTimestamp = "[" + currentTime + "] " + database.user.getUsername() + ": " + message;
                        receiverChat.appendToChat(messageWithTimestamp);
                    }

                    try {
                        // Send the message to the recipient (if applicable)
                        client.getClient().writeMessage("chat", database.user.getUsername(), user, "", message);
                    } catch (IOException ex) {
                        Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    // Lưu tin nhắn vào cơ sở dữ liệu
                    int senderId = getUserIdByUsername(database.user.getUsername());
                    int receiverId = getUserIdByUsername(user);
                    saveMessageToDatabase(database.user.getId(), receiverId, "", message, currentTime);
                }
            }
        });
        return cmd;
    }

    public void friendListConf() throws SQLException {
        friendList.removeAll();
        friendList.setModel(makeFriendList());
    }

    /*
    * makeFriendList() serch in database for all users to 
    * add them into friend list
    * @return DefaultListModel
     */
    public DefaultListModel makeFriendList() throws SQLException {
        friends = new DefaultListModel();
        PreparedStatement db_users = database.Select(new Object[]{"username", "session"}, "users");
        ResultSet db_result = db_users.executeQuery();
        while (db_result.next()) {
            if (!database.user.getUsername().equalsIgnoreCase(db_result.getString("username"))) {
                if (db_result.getString("session").equals("0")) {
                    friends.addElement(db_result.getString("username") + " offline");
                } else {
                    friends.addElement(db_result.getString("username") + " online");
                }
                System.out.println(db_result.getString("username"));
            }
        }
        return friends;
    }

    private void actions() {
        PublicEvent.getInstance().addEventImageView(new EventImageView() {
            @Override
            public void viewImage(Icon image) {
                viewImage.viewImage(image);
            }

            @Override
            public void saveImage(Icon image) {

            }

        });
        message.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    SendButton.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

        });
        friendList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    friendListRightMouseClicked(e);
                }
            }
        });
        menuProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuProfileMouseClicked(e);
            }
        });
    }

    /*
    * createChatList() create that many elements on how many
    * users are in database
     */
    public void createChatList() throws SQLException {
        chatManager = new ChatManager();
        PreparedStatement db_count = (PreparedStatement) database.connection.prepareStatement("SELECT COUNT(*) FROM users"
                + " WHERE username <> " + "'" + database.user.getUsername() + "'");
        PreparedStatement db_users = database.Select(new Object[]{"username"}, "users",
                "username <> " + "'" + database.user.getUsername() + "'");
        ResultSet users = db_users.executeQuery();
        ResultSet count = db_count.executeQuery();
        count.next();
        int rows = count.getInt(1);
        for (int i = 0; i < rows; i++) {
            chatManager.addChat(new ChatHandler("", new StringBuilder()));
        }
        for (int i = 0; i < rows; i++) {
            users.next();
            ChatHandler handler = chatManager.getFriendChatAt(i);
            handler.setUsername(users.getString("username"));
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        chatfr = new javax.swing.JLayeredPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        friendList = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        chat = new chatapplication.component.Chat_Body();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        message = new javax.swing.JTextArea();
        butMore = new javax.swing.JButton();
        SendButton = new javax.swing.JButton();
        viewImage = new chatapplication.frames.ViewImage();

        setClosable(true);
        setTitle("Chat");
        getContentPane().setLayout(new java.awt.CardLayout());

        friendList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        friendList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                friendListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(friendList);

        message.setColumns(10);
        message.setRows(1);
        message.setAutoscrolls(false);
        jScrollPane3.setViewportView(message);

        butMore.setForeground(new java.awt.Color(255, 255, 255));
        butMore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chatapplication/testPicture/more-25.png"))); // NOI18N
        butMore.setPreferredSize(new java.awt.Dimension(30, 30));
        butMore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butMoreActionPerformed(evt);
            }
        });

        SendButton.setForeground(new java.awt.Color(255, 255, 255));
        SendButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/chatapplication/testPicture/sent-25.png"))); // NOI18N
        SendButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        SendButton.setPreferredSize(new java.awt.Dimension(30, 30));
        SendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(butMore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(SendButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(SendButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(butMore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(chat, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        chatfr.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        chatfr.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout chatfrLayout = new javax.swing.GroupLayout(chatfr);
        chatfr.setLayout(chatfrLayout);
        chatfrLayout.setHorizontalGroup(
            chatfrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatfrLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        chatfrLayout.setVerticalGroup(
            chatfrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, chatfrLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chatfrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE))
                .addContainerGap())
        );

        getContentPane().add(chatfr, "card2");

        viewImage.setToolTipText("");
        getContentPane().add(viewImage, "card3");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void loadChatHistory(int senderId, int receiverId) {
        try {
            String query = "SELECT sender_id, message_text, message_icon, timestamp FROM chat_messages WHERE (sender_id = ? AND receiver_id = ? AND chat_room_id IS NULL) OR (sender_id = ? AND receiver_id = ? AND chat_room_id IS NULL) ORDER BY timestamp";
            java.sql.PreparedStatement preparedStatement = database.connection.prepareStatement(query);
            preparedStatement.setInt(1, senderId);
            preparedStatement.setInt(2, receiverId);
            preparedStatement.setInt(3, receiverId);
            preparedStatement.setInt(4, senderId);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Clear the chat window content
            chat.clear();

            // Display the chat history in the chat window
            while (resultSet.next()) {
                int messageSenderId = resultSet.getInt("sender_id");
                String messageText = resultSet.getString("message_text");
                String messageIcon = resultSet.getString("message_icon");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");

                // Determine the sender's name based on sender_id
                String senderName = getUserNameById(messageSenderId);

                // Determine the type of message (sent or received) and display accordingly
                String messageWithTimestamp = "[" + timestamp + "] " + senderName + ": " + messageText;
                if (messageSenderId == senderId) {
                    if (messageIcon == null) {
                        chat.addItemRight(messageWithTimestamp);
                    } else {
                        if (messageIcon.contains("\\chatapplication\\")){
                            chat.addItemRight(messageWithTimestamp, new ImageIcon(messageIcon));
                        }else{
                            chat.addItemRight(messageWithTimestamp, new ImageIcon(getClass().getResource(messageIcon)));
                        }
                        
                    }
                } else {
                    if (messageIcon == null) {
                        chat.addItemLeft(messageWithTimestamp);
                    }else{
                        if (messageIcon.contains("\\chatapplication\\")){
                            chat.addItemLeft(messageWithTimestamp, new ImageIcon(messageIcon));
                        }else{
                            chat.addItemLeft(messageWithTimestamp, new ImageIcon(getClass().getResource(messageIcon)));
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void receiveMessage(String fromUser, String messageText) {
        // Kiểm tra xem tin nhắn này có được gửi đến người đang trò chuyện không
        if (currentFriend != null && currentFriend.equals(fromUser)) {
            // Hiển thị tin nhắn mới trong cửa sổ chat của người bạn đang trò chuyện
            String messageWithTimestamp = fromUser + ": " + messageText + "\n";
            chat.addItemLeft(messageWithTimestamp, new ImageIcon(getClass().getResource("/chatapplication/icon/1.png"))); // Hiển thị ở bên trái
        }
        // Lưu tin nhắn vào lịch sử cuộc trò chuyện của người bạn
        StringBuilder chatHistory = chatMessages.get(fromUser);
        if (chatHistory == null) {
            chatHistory = new StringBuilder();
            chatMessages.put(fromUser, chatHistory);
        }
        chatHistory.append(fromUser + ": " + messageText + "\n");
    }

    private void friendListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_friendListMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            try {
                String friend = friendList.getSelectedValue();
                if (friend != null) {
                    idClicked = friendList.getSelectedIndex();
                    String[] friendData = friend.split(" ");
                    String selectedUsername = friendData[0];
                    user = selectedUsername; // Cập nhật biến user
                    setTitle("Chat - " + user);

                    // Gọi hàm để chuyển đổi cuộc trò chuyện và hiển thị lịch sử tin nhắn
                    int senderId = getUserIdByUsername(username); // ID của người dùng hiện tại
                    int receiverId = getUserIdByUsername(selectedUsername); // ID của người bạn đã chọn
                    loadChatHistory(senderId, receiverId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_friendListMouseClicked
    private void friendListRightMouseClicked(MouseEvent evt) {
        JList list = (JList) evt.getSource();
        int row = list.locationToIndex(evt.getPoint());
        list.setSelectedIndex(row);
        jPopupMenu1.show(this, evt.getX() + 15, evt.getY() + 35);
    }

    private void menuProfileMouseClicked(ActionEvent e) {
        String actualFriend = friendList.getSelectedValue();
        String[] data = actualFriend.split(" ");
        UserProfileFrame userProfile = new UserProfileFrame(data[0], database);
        client.getDesktop().add(userProfile);
        if (!userProfile.isVisible()) {
            userProfile.setVisible(true);
        }
    }

    // Phương thức để cập nhật trạng thái online/offline của bạn bè
    private void updateFriendStatus(String friend, String status) {
        for (int i = 0; i < friends.size(); i++) {
            Object obj = friends.get(i);
            String data = (String) obj;
            String[] splitData = data.split(" ");
            if (splitData[0].equals(friend)) {
                friends.setElementAt(friend + " " + status, i);
            }
        }
    }

    // Phương thức để làm mới cửa sổ chat
    private void refreshChatWindow() {
        this.setVisible(false);
        this.setVisible(true);
    }

    public void sendMessage(String fromUser, String toUser, String text, String textIcon, String type) {
        if (toUser.trim().equalsIgnoreCase(database.user.getUsername().trim())) {
            // Tìm ChatHandler tương ứng với người gửi tin nhắn
            ChatHandler handler = chatManager.findChatByUser(fromUser);

            // Lấy thời gian hiện tại
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = dateFormat.format(new Date());
            // Tạo nội dung tin nhắn với thời gian
            String messageTextWithTimestamp = "[" + currentTime + "] " + fromUser + ": " + text;
            String messageIcon = textIcon;

            // Kiểm tra xem cửa sổ chat hiện tại có phải là cửa sổ của người gửi
            if (user.equalsIgnoreCase(fromUser)) {
                // Nếu đúng, thêm tin nhắn vào cửa sổ chat hiện tại
                if (messageIcon == null) {
                    chat.addItemLeft(messageTextWithTimestamp);
                } else {

                    if ("chatimage".equals(type)){
                        chat.addItemLeft(messageTextWithTimestamp, new ImageIcon(messageIcon));
                    }else{
                        chat.addItemLeft(messageTextWithTimestamp, new ImageIcon(getClass().getResource(messageIcon)));
                    }
                    
                }

            }

            // Cập nhật tin nhắn trong ChatHandler
            handler.appendToChat(messageTextWithTimestamp);

            // Cập nhật trạng thái online/offline của người gửi
            updateFriendStatus(fromUser, " online");

            // Hiển thị lại cửa sổ chat
            refreshChatWindow();
        }
    }

    public void userConnected(String user) {
        try {
            for (int i = 0; i < friends.size(); i++) {
                Object obj = friends.get(i);
                String data = (String) obj;
                String[] splitData = data.split(" ");
                System.out.println(user + " = " + splitData[1]);
                if (splitData[0].equalsIgnoreCase(user)) {
                    friends.setElementAt(user + " online", i);
                }
            }
            this.setVisible(false);
            this.setVisible(true);

        } catch (Exception e) {

        }
    }

    public void userDisconnected(String user) {
        for (int i = 0; i < friends.size(); i++) {
            Object obj = friends.get(i);
            String data = (String) obj;
            String[] splitData = data.split(" ");
            System.out.println(user + " = " + splitData[1]);
            if (splitData[0].equalsIgnoreCase(user)) {
                friends.setElementAt(user + " offline", i);
            }
        }
        this.setVisible(false);
        this.setVisible(true);
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

    private void saveMessageToDatabase(int sender, int receiver, String messageText, String messageIcon, String timestamp) {
        try {
            String query = "INSERT INTO chat_messages (sender_id, receiver_id, message_text, timestamp, message_icon) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = (PreparedStatement) database.connection.prepareStatement(query);
            preparedStatement.setInt(1, sender);
            preparedStatement.setInt(2, receiver);
            preparedStatement.setString(3, messageText);
            preparedStatement.setString(5, messageIcon);
            // Sử dụng định dạng thời gian phù hợp, ví dụ: "yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = dateFormat.parse(timestamp);
            Timestamp sqlTimestamp = new Timestamp(parsedDate.getTime());
            preparedStatement.setTimestamp(4, sqlTimestamp);

            preparedStatement.executeUpdate();
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void SendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendButtonActionPerformed

        String messageText = message.getText();

        if (user != null) {
            if (!messageText.isEmpty()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTime = dateFormat.format(new Date());

                String formattedMessage = "[" + currentTime + "] " + database.user.getUsername() + ": " + messageText;

                // Display the message, including the timestamp, in the sender's chat window
                chat.addItemRight(formattedMessage); // Hiển thị tin nhắn đã gửi ngay lập tức trong cửa sổ chat của người gửi

                // Find the chat window of the recipient (if it exists)
                ChatHandler receiverChat = chatManager.getFriendChatByUser(user);

                if (receiverChat != null) {
                    // Display the message in the recipient's chat window
                    String messageWithTimestamp = "[" + currentTime + "] " + database.user.getUsername() + ": " + messageText;
                    receiverChat.appendToChat(messageWithTimestamp);
                }

                try {
                    // Send the message to the recipient (if applicable)
                    client.getClient().writeMessage("chat", database.user.getUsername(), user, messageText, "");
                } catch (IOException ex) {
                    Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

                // Clear the message input field
                message.setText("");
                // Lưu tin nhắn vào cơ sở dữ liệu
                int senderId = getUserIdByUsername(database.user.getUsername());
                int receiverId = getUserIdByUsername(user);
                saveMessageToDatabase(senderId, receiverId, messageText, "", currentTime);
            }

        } else {
            // Display an error message if no recipient is selected or the message is empty
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người để trò chuyện hoặc nhập nội dung tin nhắn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_SendButtonActionPerformed

    private void butMoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butMoreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_butMoreActionPerformed
    public void createTemporaryChatWindow(String fromUser, String text) {
        // Kiểm tra xem cửa sổ chat tạm thời đã tồn tại chưa
        if (temporaryChatWindows.containsKey(fromUser)) {
            // Nếu đã tồn tại, thêm tin nhắn vào cửa sổ chat tạm thời
            JTextArea tempChat = temporaryChatWindows.get(fromUser);
            tempChat.append(fromUser + ": " + text + "\n");
        } else {
            // Nếu chưa tồn tại, tạo mới cửa sổ chat tạm thời
            JFrame tempChatFrame = new JFrame("Temporary Chat - " + fromUser);
            JTextArea tempChat = new JTextArea();
            tempChat.setEditable(false);
            tempChat.setLineWrap(true);
            tempChat.setWrapStyleWord(true);
            tempChat.append(fromUser + ": " + text + "\n");

            // Lưu cửa sổ chat tạm thời vào map để có thể tham chiếu sau này
            temporaryChatWindows.put(fromUser, tempChat);

            // Thêm JTextArea vào JScrollPane để cuộn nội dung
            JScrollPane tempChatScrollPane = new JScrollPane(tempChat);
            tempChatFrame.getContentPane().add(tempChatScrollPane);
            tempChatFrame.setSize(400, 300);
            tempChatFrame.setVisible(true);
        }
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public Client getFrame() {
        return client;
    }

    public DatabaseManager getDatabase() {
        return database;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton SendButton;
    private javax.swing.JButton butMore;
    private chatapplication.component.Chat_Body chat;
    private javax.swing.JLayeredPane chatfr;
    private javax.swing.JList<String> friendList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea message;
    private chatapplication.frames.ViewImage viewImage;
    // End of variables declaration//GEN-END:variables

    public void setSize(JPanel desktop) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
