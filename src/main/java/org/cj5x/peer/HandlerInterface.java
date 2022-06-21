package org.cj5x.peer;

public interface HandlerInterface {
    public void handleMessage(PeerConnection conn, PeerMessage msg);
}
