package org.cj5x.peer;

import org.cj5x.peer.socket.SocketInterface;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PeerMessage {
    private byte[] type;
    private byte[] data;

    public PeerMessage(byte[] type, byte[] data) {
        this.type = (byte[]) type.clone();
        this.data = (byte[]) data.clone();
    }

    public PeerMessage(String type, String data) {
        this(type.getBytes(StandardCharsets.UTF_8), data.getBytes(StandardCharsets.UTF_8));
    }

    public PeerMessage(SocketInterface s) throws IOException {
        type = new byte[4];
        byte[] typeLen = new byte[4];

        if(s.read(type) != 4)
            throw new IOException("eof in message: type");

        if(s.read(typeLen) != 4)
            throw new IOException("eof in message: typeLen");

        int l = PeerUtilities.byteArrayToInt(typeLen);
        data = new byte[l];

        if(s.read(data) != l)
            throw new IOException("eof in message: unexpected message length");
    }

    public String getMsgType() {
        return new String(type);
    }

    public byte[] getMsgTypeBytes() {
        return (byte[]) type.clone();
    }

    public String getMsgData() {
        return new String(data);
    }

    public byte[] getMsgDataBytes() {
        return (byte[]) data.clone();
    }

    public byte[] toBytes() {
        byte[] b = new byte[4+4+data.length];
        byte[] lenB = PeerUtilities.intToByteArray(data.length);

        for(int i=0; i<4; i++) b[i] = type[i];
        for(int i=0; i<4; i++) b[i+4] = lenB[i];
        for(int i=0; i<data.length; i++) b[i+8] = data[i];


        return b;
    }

    @Override
    public String toString() {
        return "peer message [" + getMsgType() + ":" + getMsgData() +"]";
    }
}
