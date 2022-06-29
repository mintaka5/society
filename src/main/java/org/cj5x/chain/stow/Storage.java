package org.cj5x.chain.stow;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Storage {
    private Path storagePath;
    private FileOutputStream outStream;
    private FileInputStream inStream;
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
        this.outStream = new FileOutputStream(this.storagePath.toFile());
        this.inStream = new FileInputStream(this.storagePath.toFile());
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

    public FileOutputStream getOutStream() {
        return outStream;
    }

    public InputStream getInStream() {
        return inStream;
    }
}
