package ru.gb.storage.commons.messages;

import java.nio.file.Path;

public class FileDeleteMessage extends Message{
    private String deletePath;

    public FileDeleteMessage() {
    }
    public FileDeleteMessage(String deletePath) {
        this.deletePath = deletePath;
    }

    public String getDeletePath() {
        return deletePath;
    }
}
