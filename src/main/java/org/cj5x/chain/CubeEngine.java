package org.cj5x.chain;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.pkcs.PKCSException;
import org.cj5x.chain.stow.Keychain;
import org.cj5x.chain.stow.Storage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;

public class CubeEngine extends Thread {
    private Keychain keychain;
    private CubeTrain train;
    private Storage storage;

    public CubeEngine(CubeTrain cubeTrain, String passwd) {
        this.train = cubeTrain;
        this.keychain = new Keychain(
                Path.of(System.getProperty("user.home"), ".society", "society.pk8"),
                passwd
        );
        this.storage = new Storage(
                Path.of(System.getProperty("user.home"), ".society", "storage")
        );
        this.storage.setKeychain(keychain);
    }



    private void mine() {
        train.getChain().forEach(block -> {
            String prefix = StringUtils.repeat("0", block.getDifficulty());

            while(!block.getHash().startsWith(prefix)) {
                block.setNonce(block.getNonce() + 1);
                block.setHash(null); // remove old hash. don't hash the hash.
                String snapshot = block.toString();
                block.setHash(DigestUtils.sha256Hex(snapshot));
            }
        });
    }

    private Block mint() {
        Block block = new Block(
                Hex.encodeHexString(RandomUtils.nextBytes(16)),
                getTrain().getLastBlock().getHash()
        );
        train.getChain().add(block);

        return block;
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        while(true) {
            System.out.println(Instant.now().toString());
            mine();
            int waitMillis = CubeTrain.getLastBlock().getNonce() % 60 * 1000;

            try {
                // Thread.sleep(waitMillis);
                Thread.sleep(3000);

                Block mintedBlock = mint();
                encryptIt(mintedBlock);
            } catch (InterruptedException | IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                     NoSuchProviderException | PKCSException | NoSuchPaddingException | IllegalBlockSizeException |
                     BadPaddingException | InvalidKeyException e) {
                System.err.println("unable to put the thread to bed... " + e.getMessage());
            }
        }
    }

    public void encryptIt(Block b) throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchProviderException, PKCSException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException {
        String bS = b.toString();
        byte[] bB = bS.getBytes(StandardCharsets.UTF_8);
        byte[] encBB = EncryptIt.encrypt(bB, getKeychain().getPublicKey());
        FileOutputStream fos = getStorage().getOutStream();
        fos.write(encBB);
        fos.flush();
    }

    public CubeTrain getTrain() {
        return train;
    }

    public Keychain getKeychain() {
        return this.keychain;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }
}
