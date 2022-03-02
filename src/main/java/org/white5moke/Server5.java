package org.white5moke;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicInteger;

public class Server5 {
    private final int port;
    private static final int MAX_THREADS = 5;
    private final ServerSocket serverSocket;

    public Server5(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
        AtomicInteger counter = new AtomicInteger(0);
        System.out.println("server: server started.");
    }

    public static void main(String[] args) {
        try {
            new Server5(6882);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
