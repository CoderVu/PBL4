/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication.frames;

import chatapplication.ChatManager.ChatHandler;
import chatapplication.ChatManager.ChatManager;
import chatapplication.database_connection.DatabaseManager;
import chatapplication.main.Frame;
import com.mysql.jdbc.PreparedStatement;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adminn
 */
public class ChatFrame extends javax.swing.JInternalFrame{

    private DefaultListModel friends;
    private DatabaseManager database;
    private ChatManager chatManager;
    private int idClicked = 0, counter = 0;
    private String user;
    private String username;
    private JMenuItem menuProfile;
    private Frame frame;
    private Map<String, JTextArea> temporaryChatWindows = new HashMap<>() ;

    /**
     * Creates new form ChatFrame
     */
    public ChatFrame(Frame frame, String username, DatabaseManager database) {
        this.username = username;
        this.database = database;
        this.frame = frame;
        initComponents();
        menuProfile = new JMenuItem("profile");
        jPopupMenu1.add(menuProfile);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        try {
            createChatList();
        } catch (SQLException ex) {
            Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        actions();
    }

    /*
    * friendListConf() removeAll() - to delete all default elements
    * setModel to Model which we create in makeFriendList() method
     */
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
        SendButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        friendList = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        message = new javax.swing.JTextArea();
        onlineFriends = new javax.swing.JCheckBox();
        chat = new chatapplication.component.Chat_Body();

        setClosable(true);
        setTitle("Chat");

        SendButton.setText("Send");
        SendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButtonActionPerformed(evt);
            }
        });

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

        onlineFriends.setText("online friends");
        onlineFriends.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onlineFriendsMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
                            .addComponent(chat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(onlineFriends)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chat, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SendButton)
                    .addComponent(onlineFriends))
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /*
    * friendListMouseClicked() on friend in friend list clicked
    * create a specific textarea for each two users
     */
    private void friendListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_friendListMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            try {
                String friend = friendList.getSelectedValue();
                if (friend != null) {
                    idClicked = friendList.getSelectedIndex();
                    String[] friendData = friend.split(" ");
                    user = friendData[0];
                    setTitle("Chat - " + user);
                    
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
        frame.getDesktop().add(userProfile);
        if (!userProfile.isVisible()) {
            userProfile.setVisible(true);
        }
    }

    /*
    * actual receiving message from another user
     */
//    public void sendMessage(String fromUser, String toUser, String text) {
//        if (toUser.trim().equalsIgnoreCase(database.user.getUsername().trim())) {
//            StringBuilder userChat = chatManager.findChatByUser(fromUser).getChat();
//            userChat.append(fromUser + ": " + text + "\n");
//            System.out.println(user + " " + fromUser);
//
//            if (user.equalsIgnoreCase(fromUser)) {
//                chat.setText(userChat.toString());
//            }
//            for (int i = 0; i < friends.size(); i++) {
//                Object obj = friends.get(i);
//                String data = (String) obj;
//                String[] splitData = data.split(" ");
//                System.out.println(splitData[0] + splitData[1]);
//                if (splitData[0].equals(fromUser)) {
//                    friends.setElementAt(splitData[0] + " online", i);
//                }
//            }
//            this.setVisible(false);
//            this.setVisible(true);
//        }
//    }
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

    public void sendMessage(String fromUser, String toUser, String text) {
         if (toUser.trim().equalsIgnoreCase(database.user.getUsername().trim())) {
        // Tìm ChatHandler tương ứng với người gửi tin nhắn
        ChatHandler handler = chatManager.findChatByUser(fromUser);

        // Lấy thời gian hiện tại
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = dateFormat.format(new Date());

        // Tạo nội dung tin nhắn với thời gian
        String messageTextWithTimestamp = "[" + currentTime + "] " + fromUser + ": " + text + "\n";

        // Kiểm tra xem cửa sổ chat hiện tại có phải là cửa sổ của người gửi
        if (user.equalsIgnoreCase(fromUser)) {
            // Nếu đúng, thêm tin nhắn vào cửa sổ chat hiện tại
            chat.addItemLeft(messageTextWithTimestamp);
        }

        // Cập nhật tin nhắn trong ChatHandler
        handler.appendToChat(messageTextWithTimestamp);

        // Cập nhật trạng thái online/offline của người gửi
        updateFriendStatus(fromUser, "online");

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
    private void SendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendButtonActionPerformed

//    String messageText = message.getText();
//    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//    String currentTime = dateFormat.format(new Date());
//    if (user != null && !messageText.isEmpty()) {
//        String formattedMessage = currentTime + " - " + database.user.getUsername() + ": " + messageText + "\n";
//        // Hiển thị tin nhắn trong cửa sổ chat của người gửi
//        chat.append(formattedMessage);
//        
//        // Lấy cửa sổ chat của người nhận dựa trên người nhận (user)
//        ChatHandler receiverChat = chatManager.getFriendChatByUser(user);
//        
//        if (receiverChat != null) {
//            // Hiển thị tin nhắn trong cửa sổ chat của người nhận
//            receiverChat.appendToChat(database.user.getUsername() + ": " + messageText + "\n");
//        }
//
//        try {
//            // Gửi tin nhắn đến người nhận thông qua client
//            frame.getClient().writeMessage("chat", database.user.getUsername(), user, messageText);
//        } catch (IOException ex) {
//            Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        // Xóa nội dung tin nhắn sau khi gửi
//        message.setText("");
//    } else {
//        // Hiển thị thông báo cho người dùng nếu không có người nhận hoặc nội dung tin nhắn rỗng
//        JOptionPane.showMessageDialog(this, "Vui lòng chọn một người để trò chuyện hoặc nhập nội dung tin nhắn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
//    }





//  chat.addItemRight(database.user.getUsername() + ": " + message.getText());
//        ChatHandler handler = chatManager.getFriendChatAt(idClicked);
//        handler.appendToChat(database.user.getUsername() + ": " + message.getText());
//
//        try {
//            frame.getClient().writeMessage("chat", database.user.getUsername(), user, this.message.getText());
//        } catch (IOException ex) {
//            Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        this.message.setText("");


String messageText = message.getText();

    if (user != null && !messageText.isEmpty()) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = dateFormat.format(new Date());

        String formattedMessage = "[" + currentTime + "] " + database.user.getUsername() + ": " + messageText + "\n";

        // Display the message, including the timestamp, in the sender's chat window
        chat.addItemRight(formattedMessage);

        // Find the chat window of the recipient (if it exists)
        ChatHandler receiverChat = chatManager.getFriendChatByUser(user);

        if (receiverChat != null) {
            // Display the message in the recipient's chat window
            String messageWithTimestamp = "[" + currentTime + "] " + database.user.getUsername() + ": " + messageText + "\n"; 
            receiverChat.appendToChat(messageWithTimestamp);
        }

        try {
            // Send the message to the recipient (if applicable)
            frame.getClient().writeMessage("chat", database.user.getUsername(), user, messageText);
        } catch (IOException ex) {
            Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Clear the message input field
        message.setText("");
    } else {
        // Display an error message if no recipient is selected or the message is empty
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một người để trò chuyện hoặc nhập nội dung tin nhắn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_SendButtonActionPerformed
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
    private void onlineFriendsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onlineFriendsMouseClicked
        if (counter % 2 == 0) {
            Enumeration en = friends.elements();
            int j = 0;
            while (en.hasMoreElements()) {
                String obj = (String) en.nextElement();
                friends.removeElement(0);
                System.out.println("USER: " + obj);
//                if ("offline".equals((String)obj.split(" ")[1])) {
//                    friends.removeElement(obj);
//                    System.out.println("REMOVING: "+obj);
//                }
            }
        } else {
            try {
                friendListConf();
            } catch (SQLException ex) {
                Logger.getLogger(ChatFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        counter++;

    }//GEN-LAST:event_onlineFriendsMouseClicked

    

    public ChatManager getChatManager() {
        return chatManager;
    }

    public Frame getFrame() {
        return frame;
    }

    public DatabaseManager getDatabase() {
        return database;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton SendButton;
    private chatapplication.component.Chat_Body chat;
    private javax.swing.JList<String> friendList;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea message;
    private javax.swing.JCheckBox onlineFriends;
    // End of variables declaration//GEN-END:variables

    public void setSize(JPanel desktop) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
