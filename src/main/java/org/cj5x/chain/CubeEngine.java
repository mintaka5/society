package org.cj5x.chain;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.cj5x.chain.stow.Keychain;

import java.nio.file.Path;
import java.time.Instant;

public class CubeEngine extends Thread {
    private Keychain keychain;
    private CubeTrain train;

    public CubeEngine(CubeTrain cubeTrain, String passwd) {
        this.train = cubeTrain;
        this.keychain = new Keychain(
                Path.of(System.getProperty("user.home"), ".society", "society.pk8"),
                passwd
        );
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

    private void mint() {
        Block block = new Block(
                Hex.encodeHexString(RandomUtils.nextBytes(16)),
                getTrain().getLastBlock().getHash()
        );
        train.getChain().add(block);
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

                mint();
            } catch (InterruptedException e) {
                System.err.println("unable to put the thread to bed... " + e.getMessage());
            }
        }
    }

    public CubeTrain getTrain() {
        return train;
    }

    public Keychain getKeychain() {
        return this.keychain;
    }
}
