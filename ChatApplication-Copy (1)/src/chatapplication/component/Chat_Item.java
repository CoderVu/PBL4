package chatapplication.component;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import chatapplication.database_connection.DatabaseManager;
import com.mysql.jdbc.PreparedStatement;

public class Chat_Item extends javax.swing.JLayeredPane {

    private JPopupMenu popupMenu;
    private JMenuItem menuItem;
    private int messageId;
    private DatabaseManager database;

    public Chat_Item() {
        initComponents();
        initializePopupMenu();
        action();

    }

    private void initializePopupMenu() {
        menuItem = new JMenuItem("Thu hồi tin nhắn với mọi người");
        popupMenu = new JPopupMenu();
        popupMenu.add(menuItem);

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                revokeMessage();
                System.out.println("Đã vô xóa tin nhắn");
            }
        });
    }

    private void revokeMessage() {
        try {
            System.out.println("Đã vô xóa tin nhắn");
            String query = "DELETE FROM chat_messages WHERE id = ?";
            // Tạo PreparedStatement
            PreparedStatement preparedStatement = (PreparedStatement) database.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, messageId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Tin nhắn đã được thu hồi.");
                setVisible(false); // Ẩn tin nhắn sau khi đã thu hồi
            } else {
                System.out.println("Không thể thu hồi tin nhắn.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void action() {
        txt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txt = new chatapplication.swing.JIMSendTextPane();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        txt.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtMouseClicked(evt);
            }
        });
        add(txt);
    }// </editor-fold>//GEN-END:initComponents

    private void txtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMouseClicked
    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(grphcs);
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setText(String text) {
        txt.setText(text);
    }

    public void setImage(boolean right, Icon... image) {
        JLayeredPane layer = new JLayeredPane();
        layer.setLayout(new FlowLayout(right ? FlowLayout.RIGHT : FlowLayout.LEFT));
        layer.setBorder(new EmptyBorder(0, 5, 0, 5));
        Chat_Image chatImage = new Chat_Image(right);
        chatImage.addImage(image);
        layer.add(chatImage);
        add(layer);
    }

    public void hideText() {
        txt.setVisible(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private chatapplication.swing.JIMSendTextPane txt;
    // End of variables declaration//GEN-END:variables
}
