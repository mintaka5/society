package org.white5moke;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client5 implements Runnable {
    private Socket socket;
    private Scanner scan;

    public Client5(String hostAddress, int portNum) throws IOException {
        socket = new Socket(hostAddress, portNum);
        if(socket.isConnected()) {
            System.out.println("connected to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        }
        scan = new Scanner(System.in);
    }

    @Override
    public void run() {
        String clientInput;

        while(true) {
            clientInput = scan.nextLine().trim();

            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(clientInput);
                out.flush();

                BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String serverMsg = fromServer.readLine().trim();
                if(serverMsg.length() > 0) {
                    System.out.println(serverMsg);
                }
            } catch (IOException e) {}
        }
    }

    public static void main(String[] args) throws IOException {
        Client5 client = new Client5("localhost", 6887);
    }
}
