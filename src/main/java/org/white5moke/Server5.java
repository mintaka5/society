package org.white5moke;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server5 {
    private ServerSocket server;

    public Server5() throws IOException {
        server = new ServerSocket(6887);
        System.out.println("server is listening on port " + server.getLocalPort());

        String dataIn;
        Socket client = server.accept();
        System.out.println();
        String clientAddress = client.getInetAddress().getHostAddress();
        System.out.println("server: new connection from " + clientAddress);

        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream());

        while(true) {
            dataIn = in.readLine().trim();
            if(dataIn.length() > 0) {
                // print on server instance what the client said (client won't see this)
                System.out.println("client said: " + dataIn);
                // send back to the client some protocol treatment
                out.println("server said: " + dataIn.toUpperCase());
                out.flush();
            }
        }
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
