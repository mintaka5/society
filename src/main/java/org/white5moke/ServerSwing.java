package org.white5moke;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ServerSocket;

public class ServerSwing extends JFrame {
    private final ServerInstance serverInstance;
    private JPanel basePanel;
    private JPanel topPanel;
    private JTextField hostText;
    private JTextField portText;

    public ServerSwing() {
        super("society");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setSize(new Dimension(640, 480));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });

        serverInstance = new ServerInstance();
        serverInstance.start();

        buildWindow();

        setVisible(true);
    }

    private void buildWindow() {
        basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());

        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1, 10, 10));

        hostText = new JTextField();
        hostText.setText(serverInstance.host.getHostAddress());
        portText = new JTextField();


        add(basePanel);
    }

    public static void main(String[] args) {
        new ServerSwing();
    }
}
