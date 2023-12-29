
package chatapplication.frames;

import chatapplication.database_connection.DatabaseManager;
import chatapplication.rooms.RoomManager;
import com.mysql.jdbc.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Admin
 */
public class AddNewRoom extends javax.swing.JInternalFrame {

    private DatabaseManager database;
    private JCheckBox[] checkBox = new JCheckBox[1000];
    private RoomManager roomManager;
    private int id;
    private List<Integer> idInRoom = new ArrayList<>();
    
    public AddNewRoom(DatabaseManager database, int id) {
        this.database = database;
        roomManager = new RoomManager();
        this.id = id;
        initComponents();
        User();
        
    }
    
    public void UserInRoom(){
        try {
            PreparedStatement user = (PreparedStatement) database.connection.prepareStatement("select id_user from room_users where id_room = " + id);
            ResultSet rs = user.executeQuery();
            while (rs.next()){
                idInRoom.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddNewRoom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void User(){   
        try {
            UserInRoom();
            String sql = "select * from users where id != " + database.user.getId();
            for(int i = 0; i < idInRoom.size(); i++) {
                if (idInRoom.get(i) != database.user.getId()){
                    sql += " and id != " + idInRoom.get(i);
                }
                
                System.out.print(sql);
            }
            PreparedStatement user = (PreparedStatement) database.connection.prepareStatement(sql);
            ResultSet rs = user.executeQuery();
            JPanel jp = new JPanel();
            jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
            int i=0;
            while (rs.next()){
                checkBox[i] = new JCheckBox(rs.getString(2));
                checkBox[i].setActionCommand(String.valueOf(rs.getInt(1)));
                jp.add(checkBox[i]);
                i++;
            }
            jScrollPane1.setViewportView(jp);
        } catch (SQLException ex) {
            Logger.getLogger(AddNewRoom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        nameNewRoom = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();

        jLabel1.setText("Tên Phòng");

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(97, 97, 97)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(95, 95, 95))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nameNewRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameNewRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static String removeCharAt(String s, int pos) {

        return s.substring(0, pos) + s.substring(pos + 1);

    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (id==0){
            try {
                // TODO add your handling code here:
                String getmaxId = "select max(id) from rooms";
                PreparedStatement maxId = (PreparedStatement) database.connection.prepareStatement(getmaxId);
                ResultSet rs = maxId.executeQuery();
                int idRoom = 0;
                while (rs.next()){
                    idRoom = rs.getInt(1);
                }
                String getSlt = "select count(*) from room_users where id_user = " + database.user.getId();
                PreparedStatement count = (PreparedStatement) database.connection.prepareStatement(getSlt);
                ResultSet rs1 = count.executeQuery();
                int countRoom = 0;
                while (rs.next()){
                    countRoom = rs1.getInt(1);
                }
                idRoom++;
                roomManager.addRoom(countRoom, idRoom, nameNewRoom.getText());
                //JOptionPane.showMessageDialog(this, idRoom);
                String sqlAddRoom = "insert into rooms (room_name) values ('" + nameNewRoom.getText() + "')";
                PreparedStatement add = (PreparedStatement) database.getConnection().prepareStatement(sqlAddRoom);
                int rowsAffected = add.executeUpdate();
                if (rowsAffected > 0) {
                        String sqlAddUser = "insert into room_users (id_user, id_room) values (" + database.user.getId() + ", " + idRoom + ")";
                        roomManager.getRoomById(idRoom).addUser(database.user.getUsername());
                        for (int i = 0; i < checkBox.length; i++){
                            if (checkBox[i] != null) {
                                if (checkBox[i].isSelected()){
                                    sqlAddUser = sqlAddUser.concat(", (" + Integer.valueOf(checkBox[i].getActionCommand()) + ", " + idRoom + ")");
                                    roomManager.getRoomById(idRoom).addUser(checkBox[i].getText());
                                }
                            }
                        }
                        PreparedStatement addUser = (PreparedStatement) database.getConnection().prepareStatement(sqlAddUser);
                        int rowsAffectedUser = addUser.executeUpdate();
                        if (rowsAffectedUser > 0){
                            JOptionPane.showMessageDialog(this, "Create new room success");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Create new room failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                setVisible(false);
            } catch (SQLException ex) {
                Logger.getLogger(AddNewRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            try {
                String sqlAddUser = "insert into room_users (id_user, id_room) values ";
                for (int i = 0; i < checkBox.length; i++){
                    if (checkBox[i] != null) {
                        if (checkBox[i].isSelected()){
                            sqlAddUser = sqlAddUser.concat("(" + Integer.valueOf(checkBox[i].getActionCommand()) + ", " + id + "),");
                            roomManager.getRoomById(id).addUser(checkBox[i].getText());
                        }
                    }
                }
                sqlAddUser = removeCharAt(sqlAddUser, sqlAddUser.length()-1);
                PreparedStatement addUser = (PreparedStatement) database.getConnection().prepareStatement(sqlAddUser);
                int rowsAffectedUser = addUser.executeUpdate();
                if (rowsAffectedUser > 0){
                    JOptionPane.showMessageDialog(this, "Add new user success");
                }
            } catch (SQLException ex) {
                Logger.getLogger(AddNewRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
            setVisible(false);
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nameNewRoom;
    // End of variables declaration//GEN-END:variables
}
