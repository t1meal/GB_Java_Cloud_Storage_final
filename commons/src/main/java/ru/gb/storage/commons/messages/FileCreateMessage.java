package ru.gb.storage.commons.messages;

import java.nio.file.Path;

public class FileCreateMessage extends Message {
    private Path createPath;

    public FileCreateMessage() {
    }
    public FileCreateMessage(Path createPath) {
        this.createPath = createPath;
    }

    public Path getCreatePath() {
        return createPath;
    }
}
