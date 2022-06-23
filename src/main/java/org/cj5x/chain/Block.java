package org.cj5x.chain;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.time.Instant;

public class Block {
    private long timestamp;
    private String message;
    private String previousHash;
    private String hash;
    private int nonce = 0;
    private int difficulty = 2;

    public Block(String msg, String previousHash) {
        setMessage(msg.strip());
        setPreviousHash(previousHash);
        setTimestamp(Instant.now().toEpochMilli());
        String snapshot = toString();
        setHash(DigestUtils.sha256Hex(snapshot));
    }

    @Override
    public String toString() {
        JSONObject j = new JSONObject();
        j.put("timestamp", getTimestamp());
        j.put("message", getMessage());
        j.put("previousHash", getPreviousHash());
        j.put("nonce", getNonce());
        j.put("difficulty", getDifficulty());
        j.put("hash", getHash());

        return j.toString();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
