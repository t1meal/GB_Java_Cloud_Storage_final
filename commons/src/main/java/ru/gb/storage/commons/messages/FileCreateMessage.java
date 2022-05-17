package ru.gb.storage.commons.messages;

import java.nio.file.Path;

public class FileCreateMessage extends Message {
    private String createPath;

    public FileCreateMessage() {
    }
    public FileCreateMessage(Path createPath) {
        this.createPath = createPath.toString();
    }

    public String getCreatePath() {
        return createPath;
    }
}
