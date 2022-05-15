package ru.gb.storage.commons.messages;

import java.nio.file.Path;

public class FileDeleteMessage extends Message{
    private Path deletePath;


    public FileDeleteMessage() {
    }

    public FileDeleteMessage(Path deletePath) {
        this.deletePath = deletePath;

    }

    public Path getDeletePath() {
        return deletePath;
    }


}
