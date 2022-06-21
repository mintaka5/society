package org.cj5x.peer;

import org.cj5x.peer.socket.SocketFactory;
import org.cj5x.peer.socket.SocketInterface;

import java.io.IOException;

public class PeerConnection {
    private PeerInfo peerInfo;
    private SocketInterface s;

    public PeerConnection(PeerInfo info) throws IOException {
        peerInfo = info;
        s = SocketFactory.getSocketFactory().makeSocket(peerInfo.getHost(), peerInfo.getPort());
    }

    public PeerConnection(PeerInfo info, SocketInterface socket) {
        peerInfo = info;
        s = socket;
    }

    public void sendData(PeerMessage msg) {
        try {
            s.write(msg.toBytes());
        } catch (IOException e) {
            System.err.println("error sending message: " + e);
        }
    }

    public PeerMessage recvData() {
        try {
            PeerMessage msg = new PeerMessage(s);
            return msg;
        } catch (IOException e) {
            return null;
        }
    }

    public void close() {
        if(s != null) {
            try {
                s.close();
            } catch (IOException e) {
                System.err.println("error closing: " + e);
            }
            s = null;
        }
    }

    public PeerInfo getPeerInfo() {
        return peerInfo;
    }

    @Override
    public String toString() {
        return "peer connected [" + peerInfo + "]";
    }
}
