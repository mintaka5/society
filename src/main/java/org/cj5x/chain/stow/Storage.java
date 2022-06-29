package org.cj5x.chain.stow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Storage {
    private Path storagePath;

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
    }
}
