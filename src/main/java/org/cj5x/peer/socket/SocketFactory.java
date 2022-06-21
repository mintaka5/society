package org.cj5x.peer.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class SocketFactory {
    private static SocketFactory currentFactory = new NormalSocketFactory();

    public static SocketFactory getSocketFactory() {
        return currentFactory;
    }

    public static void setSocketFactory(SocketFactory sf) {
        if(sf == null)
            throw new NullPointerException("attempting t oset null socket factory");

        currentFactory = sf;
    }

    public abstract SocketInterface makeSocket(String host, int port) throws IOException;

    public abstract SocketInterface makeSocket(Socket sock) throws IOException;
}
