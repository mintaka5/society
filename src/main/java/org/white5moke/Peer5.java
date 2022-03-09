package org.white5moke;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Peer5Server {
    private final int port;
    private ServerSocket socket;

    private ExecutorService nodeExecs;

    public Peer5Server(int port) {
        this.port = port;
        this.nodeExecs = Executors.newFixedThreadPool(5);

        try {
            this.socket = new ServerSocket(this.port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Peer5Client {
    public Peer5Client() {}
}

public class Peer5 {
    public Peer5(int port) {
        new Peer5Server(port);
    }

    public static void main(String[] args) {
        new Peer5(Integer.parseInt(args[0].strip()));
    }
}
