package org.cj5x.peer.socket;

import java.io.IOException;

public interface SocketInterface {
    public void write(byte[] b) throws IOException;

    public int read() throws IOException;

    public int read(byte[] b) throws IOException;

    public void close() throws IOException;
}
