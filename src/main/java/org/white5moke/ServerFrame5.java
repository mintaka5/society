package org.white5moke;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutionException;

class ServerTask extends SwingWorker<Void, String> {
    private final ServerFrame5 window;
    private ServerSocket serverSocket;
    private int numConnections = 0;

    public ServerTask(ServerFrame5 sf) {
        window = sf;
    }

    @Override
    protected Void doInBackground() throws Exception {
        serverSocket = new ServerSocket(6882);
        publish("server running on port " + serverSocket.getLocalPort());
        while(true) {
            Socket s = serverSocket.accept();
            publish("server online. listening @ " + s.getRemoteSocketAddress());

            /*
            put threaded multiple client handling here
            i.e. new Node() -> List<HashMap>
             */
        }

    }

    @Override
    protected void process(List<String> chunks) {
        chunks.forEach(c -> {
            window.consoleText.append(String.format("%s%n", c));
        });
    }

    @Override
    protected void done() {
        publish("server has stopped.");
        stop();
    }

    private void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class ServerFrame5 extends JFrame {
    private JPanel basePanel;
    private JPanel buttonPanel;
    private JToggleButton startButton;
    private JPanel consolePanel;
    public JTextArea consoleText;
    private ServerTask serverTask;
    private JButton clientButton;

    public ServerFrame5() {
        super("society");
        setSize(new Dimension(640, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setLayout(new BorderLayout());
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                serverTask.cancel(true);
            }
        });

        serverTask = new ServerTask(this);

        buildUI();

        setVisible(true);
    }

    private void buildUI() {
        basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());

        consolePanel = new JPanel();
        consolePanel.setLayout(new BorderLayout());

        consoleText = new JTextArea(40, 10);
        consoleText.setBackground(Color.BLACK);
        consoleText.setForeground(Color.GREEN);
        consoleText.setText("awaiting...\n");
        consolePanel.add(BorderLayout.CENTER, consoleText);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 2, 2));

        startButton = new JToggleButton("start");
        clientButton = new JButton("client...");
        buttonPanel.add(startButton);
        buttonPanel.add(clientButton);

        basePanel.add(BorderLayout.SOUTH, buttonPanel);
        basePanel.add(BorderLayout.CENTER, consolePanel);
        
        eventListening();

        add(basePanel);
    }

    private void eventListening() {
        clientButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        startButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();

                if(state == ItemEvent.SELECTED) {
                    // flip the server on for listening
                    serverTask.execute();
                    startButton.setText("stop");
                } else {
                    serverTask.cancel(true);
                    startButton.setText("start");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServerFrame5();
    }
}
