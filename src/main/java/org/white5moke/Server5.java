package org.white5moke;

import org.white5moke.castlegate.Client5Handler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server5 {
    private static final int MAX_THREADS = 5;

    private ServerSocket server;
    private ExecutorService exe;

    public Server5() throws IOException {
        server = new ServerSocket(6887);
        System.out.println("server is listening on port " + server.getLocalPort());

        exe = Executors.newFixedThreadPool(MAX_THREADS);

        Socket client = server.accept();
        Client5Handler handler = new Client5Handler(client);
        exe.submit(handler);
    }

    public InetAddress getSocketAddress() {
        return server.getInetAddress();
    }

    public int getPort() {
        return server.getLocalPort();
    }

    public static void main(String[] args) throws IOException {
        Server5 server = new Server5();
    }
}
