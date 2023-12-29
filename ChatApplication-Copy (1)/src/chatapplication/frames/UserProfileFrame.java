/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication.frames;

import chatapplication.database_connection.DatabaseManager;
import com.mysql.jdbc.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class UserProfileFrame extends javax.swing.JInternalFrame {
    private String user;
    private DatabaseManager database;
    
    /**
     * Creates new form UserProfileFrame
     */
    public UserProfileFrame(String user, DatabaseManager database) {
        this.user = user;
        this.database = database;
        setTitle("Profile - "+user);
        initComponents();
        try {
            initUser();
        } catch (SQLException ex) {
            Logger.getLogger(UserProfileFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void initUser() throws SQLException{
        PreparedStatement findUser = database.Select(new Object[]{"mail"},"users","username = "+"'"+user+"'");
        ResultSet result = findUser.executeQuery();
        result.next();
        username.setText("Username: "+user);
        email.setText("E-mail: "+result.getString("mail"));
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        username = new javax.swing.JLabel();
        email = new javax.swing.JLabel();
        info = new javax.swing.JLabel();

        setClosable(true);

        username.setText("jLabel1");

        email.setText("jLabel1");

        info.setText("Basic Informations: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(info)
                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(email))
                .addGap(0, 61, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(info)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(email)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel email;
    private javax.swing.JLabel info;
    private javax.swing.JLabel username;
    // End of variables declaration//GEN-END:variables
}
