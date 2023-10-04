/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package chatapplication.component;

import net.miginfocom.swing.MigLayout;
import chatapplication.swing.ScrollBar;
import javax.swing.JLabel;

public final class Chat_Body extends javax.swing.JPanel {

    public Chat_Body() {
        initComponents();
        init();
        
    }
    private void init(){
        body.setLayout(new MigLayout("fillx"));
        sp.setVerticalScrollBar(new ScrollBar());
    }
    public void addItemLeft(String text){
        Chat_Left item = new Chat_Left();
        item.setText(text);
        body.add(item, "wrap, w ::80%");
        body.repaint();
        body.revalidate();
        
    }
    public void addItemRight(String text){
        Chat_Right item = new Chat_Right();
        item.setText(text);
        body.add(item, "wrap, al right, w ::80%");
        body.repaint();
        body.revalidate();
        
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sp = new javax.swing.JScrollPane();
        body = new javax.swing.JPanel();

        sp.setBorder(null);

        body.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout bodyLayout = new javax.swing.GroupLayout(body);
        body.setLayout(bodyLayout);
        bodyLayout.setHorizontalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 543, Short.MAX_VALUE)
        );
        bodyLayout.setVerticalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 422, Short.MAX_VALUE)
        );

        sp.setViewportView(body);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel body;
    private javax.swing.JScrollPane sp;
    // End of variables declaration//GEN-END:variables

    public void append(String messageText) {
    // Create a new component (e.g., JLabel) to display the messageText
    JLabel messageLabel = new JLabel(messageText);
    
    // Add the messageLabel to the body panel
    body.add(messageLabel, "wrap, w ::80%");
    
    // Repaint and revalidate the body panel to reflect the changes
    body.repaint();
    body.revalidate();
}

}
