package ru.gb.storage.commons.messages;

public class FileSendMessage extends Message {
    String path;
    String fileName;
    String currentPath;

    public FileSendMessage() {
    }

    public FileSendMessage(String path, String fileName, String currentPath) {
        this.path = path;
        this.fileName = fileName;
        this.currentPath = currentPath;
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCurrentPath() {
        return currentPath;
    }
}
