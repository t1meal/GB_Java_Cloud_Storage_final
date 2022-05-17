package ru.gb.storage.commons.messages;


import ru.gb.storage.commons.FilesInfo;

import java.util.List;

public class StorageMessage extends Message {
    private String path;
    private List<FilesInfo> list;
    private String nick;
    private Integer initialStatus = 0;

    public StorageMessage() {
    }
    public StorageMessage(String path) {
        this.path = path;
    }

    public List<FilesInfo> getList() {
        return list;
    }
    public void setList(List<FilesInfo> list) {
        this.list = list;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getNick() {return nick;}
    public void setNick(String nick) {this.nick = nick;}
    public Integer getInitialStatus() {return initialStatus;}
    public void setInitialStatus(Integer initialStatus) {this.initialStatus = initialStatus;}
}
