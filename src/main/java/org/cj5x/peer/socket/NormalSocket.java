package org.cj5x.peer.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NormalSocket implements SocketInterface {
    private Socket s;
    private InputStream inStream;
    private OutputStream outStream;

    public NormalSocket(String host, int port) throws IOException {
        this(new Socket(host, port));
    }

    public NormalSocket(Socket socket) throws IOException {
        s = socket;
        inStream = s.getInputStream();
        outStream = s.getOutputStream();
    }

    @Override
    public void write(byte[] b) throws IOException {
        outStream.write(b);
        outStream.flush();
    }

    @Override
    public int read() throws IOException {
        return inStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return inStream.read(b);
    }

    @Override
    public void close() throws IOException {
        inStream.close();
        outStream.close();
        s.close();
    }
}
