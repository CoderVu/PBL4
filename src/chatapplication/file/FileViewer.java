///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package chatapplication.file;
//
///**
// *
// * @author ngmv1
// */
//import javafx.application.Platform;
//import javafx.embed.swing.JFXPanel;
//import javafx.scene.Scene;
//import javafx.scene.web.WebView;
//import javax.swing.*;
//import java.awt.*;
//import java.net.MalformedURLException;
//import java.net.URL;
//import org.eclipse.persistence.internal.databaseaccess.Platform;
//
//public class FileViewer extends JFrame {
//    private JFXPanel jfxPanel = new JFXPanel();
//
//    public FileViewer(String filePath) {
//        initSwingComponents(filePath);
//        initFX(filePath);
//    }
//
//    private void initSwingComponents(String filePath) {
//        setLayout(new BorderLayout());
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//        add(jfxPanel, BorderLayout.CENTER);
//        setSize(800, 600);
//        setLocationRelativeTo(null);
//    }
//
//    private void initFX(String filePath) {
//        Platform.runLater(() -> {
//            WebView webView = new WebView();
//            jfxPanel.setScene(new Scene(webView));
//            try {
//                URL url = new URL("file:///" + filePath);
//                webView.getEngine().load(url.toExternalForm());
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    public static void viewFile(String filePath) {
//        SwingUtilities.invokeLater(() -> {
//            FileViewer viewer = new FileViewer(filePath);
//            viewer.setVisible(true);
//        });
//    }
//}
