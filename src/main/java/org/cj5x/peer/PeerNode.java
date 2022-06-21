package org.cj5x.peer;

import org.apache.commons.codec.digest.DigestUtils;
import org.cj5x.peer.socket.SocketFactory;
import org.cj5x.peer.socket.SocketInterface;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

class PeerUtilities {
    public static String getExternalHostname() {
        String host = "";

        try {
            Socket s = new Socket("lookup.icann.org", 80);
            host = s.getLocalAddress().getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("unable to acquire host. " + e.getMessage());
        } catch (IOException e) {
            System.out.println("unable to establish host. " + e.getMessage());
        }

        return host;
    }

    public static byte[] intToByteArray(final int i) {
        int byteNum = (40 - Integer.numberOfLeadingZeros(i < 0 ? ~i : i));
        byte[] ba = new byte[4];

        for(int n=0; n<byteNum; n++)
            ba[3-n] = (byte) (i >>> (n*8));

        return (ba);
    }

    public static int byteArrayToInt(byte[] ba) {
        int i = 0;
        for(int n=0; n<4; n++) {
            i = (i << 8) | (((int) ba[n]) & 0xff);
        }

        return i;
    }
}

public class PeerNode {
    private class PeerHandler extends Thread {
        private SocketInterface s;

        public PeerHandler(Socket socket) throws IOException {
            s = SocketFactory.getSocketFactory().makeSocket(socket);
        }

        public void run() {
            System.out.println("new peer: " + s);

            PeerConnection peerConn = new PeerConnection(null, s);
            PeerMessage peerMsg = peerConn.recvData();

            if(!handlers.containsKey(peerMsg.getMsgType())) {
                System.err.println("not handled: " + peerMsg);
            } else {
                System.out.println("handling: " + peerMsg);
                handlers.get(peerMsg.getMsgType()).handleMessage(peerConn, peerMsg);
            }
            System.out.println("disconnecting incoming: " + peerConn);

            peerConn.close();
        }
    }

    private class StabilizerRunner extends Thread {
        private StabilizerInterface st;
        private int delay; // milliseconds

        public StabilizerRunner(StabilizerInterface st, int delay) {
            this.st = st;
            this.delay = delay;
        }

        public void run() {
            while(true) {
                st.stabilizer();
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }
        }
    }

    private static final int SOCKET_TIMEOUT = 2000;
    private int maxPeers;
    private PeerInfo peerInfo;
    private Hashtable<String, PeerInfo> peers = new Hashtable<String, PeerInfo>();
    private Hashtable<String, HandlerInterface> handlers = new Hashtable<String, HandlerInterface>();
    private RouterInterface router = null;
    private boolean shutdown = false;

    public PeerNode(int maxPeers, PeerInfo info) {
        this.maxPeers = maxPeers;
        this.peerInfo = info;

        if(info.getHost() == null)
            info.setHost(PeerUtilities.getExternalHostname());

        if(info.getId() == null)
            info.setId(DigestUtils.sha256Hex(
                    info.getHost() + ":" + String.valueOf(info.getPort()))
            );
    }

    public PeerNode(int port) {
        this(0, new PeerInfo(port));
    }

    public ServerSocket makeServerSocket(int port) throws IOException {
        return makeServerSocket(port, 5);
    }

    public ServerSocket makeServerSocket(int port, int backLog) throws IOException {
        ServerSocket s = new ServerSocket(port, backLog);
        s.setReuseAddress(true);

        return s;
    }

    public List<PeerMessage> sendToPeer(String peerId, String msgType, String msgData, boolean waitReply) throws IOException {
        PeerInfo pd = null;
        if(router != null)
            pd = router.route(peerId);
        if(pd == null) {
            System.err.println("unable to route " + msgType + " to " + peerId);

            return new ArrayList<PeerMessage>();
        }

        return connectAndSend(pd, msgType, msgData, waitReply);
    }

    private List<PeerMessage> connectAndSend(PeerInfo pd, String msgType, String msgData, boolean waitReply) throws IOException {
        List<PeerMessage> msgReply = new ArrayList<PeerMessage>();

        PeerConnection peerConn = new PeerConnection(pd);
        PeerMessage toSend = new PeerMessage(
                msgType.getBytes(StandardCharsets.UTF_8),
                msgData.getBytes(StandardCharsets.UTF_8)
        );
        peerConn.sendData(toSend);
        System.out.println("sent " + toSend + "/" + peerConn);

        if(waitReply) {
            PeerMessage oneReply = peerConn.recvData();
            while(oneReply != null) {
                msgReply.add(oneReply);
                System.out.println("got reply " + oneReply);
                oneReply = peerConn.recvData();
            }
        }

        peerConn.close();

        return msgReply;
    }

    public void mainLoop() {
        try {
            ServerSocket s = makeServerSocket(peerInfo.getPort());
            s.setSoTimeout(SOCKET_TIMEOUT);

            while(!shutdown) {
                System.out.println("listening...");
                Socket clientSock = s.accept();
                clientSock.setSoTimeout(0);

                PeerHandler ph = new PeerHandler(clientSock);
            }
        } catch (IOException e) {
            System.err.println("stopping main loop (ioexc): " + e);
        }
    }

    public Set<String> getPeerKeys() {
        return peers.keySet();
    }

    public PeerInfo getPeer(String peerId) {
        return peers.get(peerId);
    }

    public void addRouter(RouterInterface router) {
        this.router = router;
    }

    public void addHandler(String msgType, HandlerInterface handler) {
        handlers.put(msgType, handler);
    }

    public boolean maxPeersReached() {
        return maxPeers > 0 && peers.size() == maxPeers;
    }

    public int getMaxPeers() {
        return maxPeers;
    }

    public String getId() {
        return peerInfo.getId();
    }

    public boolean addPeer(PeerInfo info) {
        return addPeer(info.getId(), info);
    }

    public boolean addPeer(String key, PeerInfo info) {
        if((maxPeers == 0 || peers.size() < maxPeers) && !peers.containsKey(key)) {
            peers.put(key, info);
            return true;
        }

        return false;
    }

    public int getNumberOfPeers() {
        return peers.size();
    }

    public static void main(String[] args) {
        if(args.length > 0) {
            int port = Integer.parseInt(args[0].strip());
        }
    }
}
