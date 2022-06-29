package org.cj5x.chain.stow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Storage {
    private Path storagePath;
    private BufferedWriter outStream;
    private BufferedReader inStream;
    private Keychain keychain;

    public Storage(Path p) {
        this.storagePath = p;

        if(!Files.exists(this.storagePath)) {
            try {
                Files.createDirectories(this.storagePath.getParent());
                Files.createFile(this.storagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void init() throws IOException {
        this.outStream = Files.newBufferedWriter(this.storagePath, StandardOpenOption.WRITE);
        this.inStream = Files.newBufferedReader(this.storagePath);
    }

    public void close() throws IOException {
        this.inStream.close();
        this.outStream.close();
    }

    public void flushAndClose() throws IOException {
        this.outStream.flush();
        close();
    }

    public void setKeychain(Keychain kc) {
        this.keychain = kc;
    }

    public BufferedWriter getOutStream() {
        return outStream;
    }

    public BufferedReader getInStream() {
        return inStream;
    }
}
