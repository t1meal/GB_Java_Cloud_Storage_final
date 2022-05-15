package ru.gb.storage.commons.messages;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


public class FileRequestMessage extends Message {
    private String path;
    private String name;

    public FileRequestMessage() {
    }

    public FileRequestMessage(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getPath () {return path;}


    public String getName() {
        return name;
    }
}


