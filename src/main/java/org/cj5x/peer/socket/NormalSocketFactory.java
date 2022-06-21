package org.cj5x.peer.socket;

import java.io.IOException;
import java.net.Socket;

public class NormalSocketFactory extends SocketFactory {
    public SocketInterface makeSocket(String host, int port) throws IOException {
        return new NormalSocket(host, port);
    }

    @Override
    public SocketInterface makeSocket(Socket sock) throws IOException {
        return new NormalSocket(sock);
    }
}
