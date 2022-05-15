package ru.gb.storage.commons;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesInfo {

    private String name;
    private long size;

    private Path path;

    public FilesInfo(Path path) {
        try {
            this.name = path.getFileName().toString();
            if (Files.isDirectory(path)){
                this.size = -1L;
            } else {
                this.size = Files.size(path);
            }
            this.path = path;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FilesInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Path getPath() {
        return path;
    }
}
