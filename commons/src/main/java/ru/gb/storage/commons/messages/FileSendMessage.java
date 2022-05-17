package ru.gb.storage.commons.messages;

import java.nio.file.Path;

public class FileSendMessage extends Message {
    String filePath;
    String fileName;
    String currentCloudPath;

    public FileSendMessage() {
    }

    public FileSendMessage(Path filePath, String fileName, Path currentCloudPath) {
        this.filePath = filePath.toString();
        this.fileName = fileName;
        this.currentCloudPath = currentCloudPath.toString();
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCurrentCloudPath() {
        return currentCloudPath;
    }
}
