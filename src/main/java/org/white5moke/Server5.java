package org.white5moke;

import org.apache.commons.codec.binary.Hex;
import org.white5moke.castlegate.Client5Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Server5 {
    private final int port;
    private static final int MAX_THREADS = 5;
    private final ServerSocket serverSocket;

    private List<Client5Handler> clients = new ArrayList<>();

    public Server5(int port) throws IOException, NoSuchAlgorithmException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);

        System.out.println(">> server started.");
        while(true) {
            // get a random string to assign as client ID
            String clientID = Utilities.randomID(4);

            Socket client = serverSocket.accept();
            System.out.println(">> " + "client [" + clientID + "] started.");
            Client5Handler client5Handler = new Client5Handler(client, clientID);
            client5Handler.start();
            clients.add(client5Handler);
        }
    }

    public static void main(String[] args) {
        try {
            new Server5(6882);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
