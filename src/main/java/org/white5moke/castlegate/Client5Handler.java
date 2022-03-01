package org.white5moke.castlegate;

import java.io.*;
import java.net.Socket;

public class Client5Handler implements Runnable {
    private final Socket socket;

    public Client5Handler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream;
        BufferedReader bufferedReader = null;
        DataOutputStream outputStream = null;

        try {
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line;

        while(true) {
            try {
                line = bufferedReader.readLine();
                if((line == null) || line.equalsIgnoreCase("quit")) {
                    socket.close();
                    return;
                } else {
                    outputStream.writeBytes(line + "\n\r");
                    outputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
