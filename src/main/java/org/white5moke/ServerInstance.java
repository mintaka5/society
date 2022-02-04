package org.white5moke;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class ServerInstance extends Thread {
    public int port = 6881;
    public InetAddress host;
    public ServerSocket serverSocket;

    public ServerInstance() {
        host = InetAddress.getLoopbackAddress();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {

        }
    }
}
