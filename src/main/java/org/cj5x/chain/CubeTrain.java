package org.cj5x.chain;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CubeTrain {
    private static List<Block> chain = new ArrayList<Block>();

    public CubeTrain(String salt) {
        genesis(salt);
    }

    private void genesis(String salt) {
        if(chain.isEmpty()) {
            Block b = new Block("value is soul!", DigestUtils.sha256Hex(RandomUtils.nextBytes(8)));
            chain.add(b);
        }
    }

    public List<Block> getChain() {
        return chain;
    }

    public static KeyPair generateSigningKey(String salt, int keySize)
            throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("EC");
        gen.initialize(keySize, SecureRandom.getInstance("SHA1PRNG"));
        KeyPair keyPair = gen.generateKeyPair();
        System.out.println("secret: " + Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        System.out.println("public: " + Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        return keyPair;
    }

    public static Block getLastBlock() {
        return chain.stream().min((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp())).orElseGet(null);
    }

    public static void main (String[] args) {
        CubeTrain ct = new CubeTrain("cj5x");
        CubeEngine ce = new CubeEngine(ct);
        ce.start();
    }
}
