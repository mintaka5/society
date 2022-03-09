package org.white5moke;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client5 {
    private final String host;
    private final int port;
    private final Socket socket;

    public Client5(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        socket = new Socket(this.host, this.port);

        DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream inStream = new DataInputStream(socket.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String clientMessage = "", serverMessage = "";
        while(!clientMessage.equalsIgnoreCase("BYE")) {
            System.out.print("client: enter something: ");
            clientMessage = reader.readLine();

            outStream.writeUTF(clientMessage);
            outStream.flush();
            serverMessage = inStream.readUTF();
            System.out.println("server: " + serverMessage);
        }

        System.out.println("client: disconnecting from host " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        outStream.close();
        inStream.close();
        socket.close();
    }

    public static void main(String[] args) {
        try {
            new Client5("localhost", Integer.parseInt(args[0].strip()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
