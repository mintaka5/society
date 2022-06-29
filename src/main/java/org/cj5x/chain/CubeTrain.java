package org.cj5x.chain;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.util.io.pem.PemObject;
import org.cj5x.chain.stow.Keychain;
import org.codehaus.plexus.components.io.attributes.FileAttributes;
import org.json.JSONArray;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class CubeTrain {
    private static List<Block> chain = new ArrayList<Block>();
    private Keychain keychain;

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

    public static Block getLastBlock() {
        return chain.stream().min((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp())).orElseGet(null);
    }

    public static void main (String[] args) {
        String passwd = args[0].strip();
        CubeTrain ct = new CubeTrain(passwd);
        CubeEngine ce = new CubeEngine(ct, passwd);
        ct.setKeychain(ce.getKeychain());
        ce.start();

        Runtime.getRuntime().addShutdownHook(new Shutdown(ce));
    }

    public void setKeychain(Keychain keyChain) {
        this.keychain = keyChain;
    }

    private static class Shutdown extends Thread {
        private final CubeEngine cubeEngine;

        public Shutdown(CubeEngine cubeEngine) {
            this.cubeEngine = cubeEngine;
        }
        @Override
        public void run() {
            Path p = Path.of(System.getProperty("user.home"), ".society", "storage");
            if(!Files.exists(p)) {
                try {
                    Files.createDirectories(p.getParent());
                    Files.createFile(p);
                } catch (IOException e) {
                    System.err.println("unable to create path. " + e);
                }
            }

            JSONArray jA = new JSONArray();
            cubeEngine.getTrain().getChain().forEach(block -> {
                jA.put(block.toJSON());
            });
            String jS = jA.toString();

            try {
                PrivateKey privKey = this.cubeEngine.getKeychain().fromFile();
                RSAPrivateCrtKey privCert = (RSAPrivateCrtKey) privKey;
                RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(privCert.getModulus(), privCert.getPublicExponent());
                KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
                PublicKey pubKey = factory.generatePublic(pubSpec);
                byte[] encChain = EncryptIt.encrypt(jS.getBytes(StandardCharsets.UTF_8), pubKey);
                OutputStream writer = Files.newOutputStream(p, StandardOpenOption.TRUNCATE_EXISTING);
                writer.write(encChain);
                writer.flush();
                writer.close();
            } catch (IOException | PKCSException | InvalidKeySpecException
                     | NoSuchAlgorithmException | NoSuchProviderException
                     | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                     | BadPaddingException e) {
                e.printStackTrace();
            }


            System.out.println("shutting down...");
        }
    }
}
