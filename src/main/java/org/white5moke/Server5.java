package org.white5moke;

import org.white5moke.castlegate.Client5Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Server5 {
    private final int port;
    private static final int MAX_THREADS = 5;
    private final ServerSocket serverSocket;

    public Server5(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
        AtomicInteger counter = new AtomicInteger(0);
        System.out.println(">> server started.");
        while(true) {
            int count = counter.incrementAndGet();
            Socket client = serverSocket.accept();
            System.out.println(">> " + "client [" + count + "] started.");
            Client5Handler client5Handler = new Client5Handler(client, count);
            client5Handler.start();
        }
    }

    public static void main(String[] args) {
        try {
            new Server5(8001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
