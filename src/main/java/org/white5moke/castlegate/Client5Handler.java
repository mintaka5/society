package org.white5moke.castlegate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client5Handler extends Thread {
    private Socket serverClient;
    private int clientIndex;


    public Client5Handler(Socket client, int index) {
        serverClient = client;
        clientIndex = index;
    }

    @Override
    public void run() {
        try {
            DataInputStream inStream = new DataInputStream(serverClient.getInputStream());
            DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());

            String clientMessage = "";

            while(!clientMessage.equalsIgnoreCase("BYE")) {
                clientMessage = inStream.readUTF();
                if(clientMessage.length() > 0) {
                    System.out.println("CLIENT [" + clientIndex + "]: " + clientMessage);
                    // after here do stuff on the server
                    outStream.writeUTF("BIGGER! " + clientMessage.toUpperCase());
                    outStream.flush();
                }
            }

            inStream.close();
            outStream.close();
            serverClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
