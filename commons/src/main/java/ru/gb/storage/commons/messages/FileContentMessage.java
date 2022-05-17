package ru.gb.storage.commons.messages;

public class FileContentMessage extends Message {
    private String name;
    private byte[] content;
    private long startPosition;
    private String currentCloudPath;

    public FileContentMessage() {
    }

    private boolean last;

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCurrentCloudPath() {return currentCloudPath;}

    public void setCurrentCloudPath(String currentCloudPath) {this.currentCloudPath = currentCloudPath;}
}

