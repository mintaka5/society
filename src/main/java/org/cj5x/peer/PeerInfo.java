package org.cj5x.peer;

import org.apache.commons.codec.digest.DigestUtils;

public class PeerInfo {
    private String id;
    private String host;
    private int port;

    public PeerInfo(int port) {
        String host = PeerUtilities.getExternalHostname();
        this.id = DigestUtils.sha256Hex(host + ":" + String.valueOf(port));
        this.host = host;
        this.port = port;
    }

    public PeerInfo(String id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
