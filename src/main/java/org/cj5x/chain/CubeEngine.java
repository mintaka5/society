package org.cj5x.chain;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

public class CubeEngine extends Thread {
    private CubeTrain train;

    public CubeEngine(CubeTrain cubeTrain) {
        this.train = cubeTrain;
    }

    private void mine() {
        train.getChain().forEach(block -> {
            String prefix = StringUtils.repeat("0", block.getDifficulty());

            while(!block.getHash().startsWith(prefix)) {
                block.setNonce(block.getNonce() + 1);
                block.setHash(null); // remove old hash so we don't hash that as well
                String snapshot = block.toString();
                block.setHash(DigestUtils.sha256Hex(snapshot));
            }
        });
    }

    private void mint() {
        Block block = new Block(Hex.encodeHexString(RandomUtils.nextBytes(16)), train.getLastBlock().getHash());
        train.getChain().add(block);
    }

    @Override
    public void run() {
        while(true) {
            System.out.println(Instant.now().toString());
            mine();
            int waitMillis = CubeTrain.getLastBlock().getNonce() % 60 * 1000;

            train.getChain().forEach(block -> System.out.println(block.toString()));

            try {
                Thread.sleep(waitMillis);

                mint();
            } catch (InterruptedException e) {
                System.err.println("unable to put the thread to bed... " + e.getMessage());
            }
        }
    }
}
