package org.cj5x.peer.node;

import org.cj5x.peer.*;

import java.nio.charset.StandardCharsets;

public class ChatPeerNode extends PeerNode {
    public static final String INSERTPEER = "JOIN";
    public static final String LISTPEER = "LIST";
    public static final String PEERNAME = "NAME";
    public static final String QUERY = "QUER";
    public static final String QRESPONSE = "RESP";
    public static final String PEERQUIT = "QUIT";

    public static final String REPLY = "REPL";
    public static final String ERROR = "ERRO";

    public ChatPeerNode(int maxPeers, PeerInfo info) {
        super(maxPeers, info);

        this.addRouter(new Router(this));

        this.addHandler(INSERTPEER, new JoinHandler(this));
        this.addHandler(LISTPEER, new ListHandler(this));
    }

    private class ListHandler implements HandlerInterface {
        private PeerNode peer;

        public ListHandler(PeerNode peer) {this.peer = peer;}

        @Override
        public void handleMessage(PeerConnection conn, PeerMessage msg) {
            conn.sendData(new PeerMessage("REPLY", String.format("%d", peer.getNumberOfPeers())));
            for(String pid: peer.getPeerKeys()) {
                conn.sendData(new PeerMessage(REPLY, String.format(
                        "%s %s %d",
                        pid,
                        peer.getPeer(pid).getHost(),
                        peer.getPeer(pid).getPort())));
            }
        }
    }

    private class JoinHandler implements HandlerInterface {
        private PeerNode peer;

        public JoinHandler(PeerNode peer) {
            this.peer = peer;
        }

        @Override
        public void handleMessage(PeerConnection conn, PeerMessage msg) {
            if(peer.maxPeersReached()) {
                System.out.println("max peers reached " + peer.getMaxPeers());
                conn.sendData(new PeerMessage(ERROR, "join: too many peers."));
                return;
            }

            String[] data = msg.getMsgData().split(" ");
            if(data.length != 3) {
                conn.sendData(new PeerMessage(ERROR, "join: incorrect arguments"));
                return;
            }

            PeerInfo info = new PeerInfo(data[0], data[1], Integer.parseInt(data[2]));

            if(peer.getPeer(info.getId()) != null)
                conn.sendData(new PeerMessage(ERROR, "join: peer already inserted"));
            else if(info.getId().equals(peer.getId()))
                conn.sendData(new PeerMessage(ERROR, "join: attempt to insert self"));
            else {
                peer.addPeer(info);
                conn.sendData(new PeerMessage(REPLY, "join: peer added " + info.getId()));
            }
        }
    }

    private class Router implements RouterInterface {
        private PeerNode peer;

        public Router(PeerNode peer) {
            this.peer = peer;
        }

        @Override
        public PeerInfo route(String peerId) {
            if(peer.getPeerKeys().contains(peerId))
                return peer.getPeer(peerId);
            else
                return null;
        }
    }
}
