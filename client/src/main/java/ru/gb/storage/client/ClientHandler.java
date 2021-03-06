package ru.gb.storage.client;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.storage.commons.messages.FileContentMessage;
import ru.gb.storage.commons.messages.FileSendMessage;
import ru.gb.storage.commons.messages.Message;
import ru.gb.storage.commons.messages.StorageMessage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.Objects;

public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private static ClientHandler handler;
    private MainController mainController;

    public static ClientHandler getInstance(){
        if (Objects.isNull(handler)){
            handler = new ClientHandler();
        }
        return handler;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        System.out.println("New message " + msg.getClass().toString());
        if (msg instanceof StorageMessage){
            StorageMessage storageMessage = (StorageMessage) msg;
            mainController.refreshCloudTable(storageMessage.getList());
            mainController.setCloudPath(storageMessage.getPath());
        }
        if(msg instanceof FileContentMessage){
            FileContentMessage fileContentMessage = (FileContentMessage) msg;
            String fileName = fileContentMessage.getName();
            String currentPath = mainController.getCurrentClientPath();
            String fullPath = Paths.get(currentPath).resolve(fileName).toString();

            RandomAccessFile accessFile = new RandomAccessFile(fullPath, "rw");
            System.out.println(fileContentMessage.getStartPosition());
            accessFile.seek(fileContentMessage.getStartPosition());
            accessFile.write(fileContentMessage.getContent());
            mainController.refreshClientTable(Paths.get(currentPath));
        }
        if (msg instanceof FileSendMessage){
            FileSendMessage fileSendMessage = (FileSendMessage) msg;
            String path = fileSendMessage.getPath();
            String fileName = fileSendMessage.getFileName();
            RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
            sendFile(randomAccessFile, ctx, fileName, fileSendMessage.getCurrentPath());
        }
    }

    private void sendFile(RandomAccessFile randomAccessFile, ChannelHandlerContext ctx, String fileName, String currentPath) throws IOException {
        byte[] partOfFile;
        final long available = randomAccessFile.length() - randomAccessFile.getFilePointer();
        if (available > 64 * 1024) {
            partOfFile = new byte[64 * 1024];
        } else {
            partOfFile = new byte[(int) available];
        }
        FileContentMessage contentMessage = new FileContentMessage();
        contentMessage.setName(fileName);
        contentMessage.setStartPosition(randomAccessFile.getFilePointer());
        randomAccessFile.read(partOfFile);
        contentMessage.setContent(partOfFile);
        final boolean last = randomAccessFile.getFilePointer() == randomAccessFile.length();
        contentMessage.setLast(last);

        ctx.channel().writeAndFlush(contentMessage).addListener((ChannelFutureListener) future -> {
            if (!last) {
                sendFile(randomAccessFile, ctx, fileName, currentPath);
            }
        });
        if (last) {
            randomAccessFile.close();
            NetworkController.send(new StorageMessage(currentPath));
        }
    }
}
