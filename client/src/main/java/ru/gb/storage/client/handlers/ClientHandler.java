package ru.gb.storage.client.handlers;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.storage.client.fx.MainController;
import ru.gb.storage.client.servises.NetworkController;
import ru.gb.storage.commons.messages.*;

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
    public void channelActive(ChannelHandlerContext ctx) {System.out.println("Channel is active!");}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        System.out.println("New message " + msg.getClass().toString());

        if (msg instanceof AuthorizationMessage){
            AuthorizationMessage message = (AuthorizationMessage) msg;
            if (message.getAuthorizationStatus()){
                StorageMessage storageMessage = new StorageMessage(null);
                String nick = message.getNick();
                storageMessage.setNick(nick);
                MainController.setIsAuthorized(true);
//                mainController.setCurrentNick(nick);
                System.out.println("Авторизация прошла успешно!");
                ctx.writeAndFlush(storageMessage);
            }
        }
        if (msg instanceof StorageMessage){
            StorageMessage storageMessage = (StorageMessage) msg;
            if (storageMessage.getInitialStatus() == 1) {
                mainController.setPrimaryCloudPath(storageMessage.getPath());
            }
            mainController.refreshCloudTable(storageMessage.getList());
            mainController.setCloudPath(storageMessage.getPath());
        }
        if(msg instanceof FileContentMessage){
            FileContentMessage contentMessage = (FileContentMessage) msg;
            String fileName = contentMessage.getName();
            String currentPath = mainController.getCurrentClientPath();
            String fullPath = Paths.get(currentPath).resolve(fileName).toString();

            RandomAccessFile accessFile = new RandomAccessFile(fullPath, "rw");
            if (contentMessage.isLast()){
                System.out.println(contentMessage.getStartPosition());
                accessFile.seek(contentMessage.getStartPosition());
                accessFile.write(contentMessage.getContent());
                mainController.refreshClientTable(Paths.get(currentPath));
                accessFile.close();
            } else {
                System.out.println(contentMessage.getStartPosition());
                accessFile.seek(contentMessage.getStartPosition());
                accessFile.write(contentMessage.getContent());
                mainController.refreshClientTable(Paths.get(currentPath));
            }
        }
        if (msg instanceof FileSendMessage){
            FileSendMessage fileSendMessage = (FileSendMessage) msg;
            String path = fileSendMessage.getFilePath();
            String fileName = fileSendMessage.getFileName();
            String currentCloudPath = fileSendMessage.getCurrentCloudPath();
            RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
            sendFile(randomAccessFile, ctx, fileName, currentCloudPath);
        }
    }

    private void sendFile(RandomAccessFile randomAccessFile, ChannelHandlerContext ctx, String fileName, String currentCloudPath) throws IOException {
        byte[] partOfFile;
        final long available = randomAccessFile.length() - randomAccessFile.getFilePointer();
        if (available > 64 * 1024) {
            partOfFile = new byte[64 * 1024];
        } else {
            partOfFile = new byte[(int) available];
        }
        FileContentMessage fileContentMessage = new FileContentMessage();
        fileContentMessage.setName(fileName);
        fileContentMessage.setCurrentCloudPath(currentCloudPath);
        fileContentMessage.setStartPosition(randomAccessFile.getFilePointer());
        randomAccessFile.read(partOfFile);
        fileContentMessage.setContent(partOfFile);
        final boolean last = randomAccessFile.getFilePointer() == randomAccessFile.length();
        fileContentMessage.setLast(last);

        ctx.channel().writeAndFlush(fileContentMessage).addListener((ChannelFutureListener) future -> {
            if (!last) {
                sendFile(randomAccessFile, ctx, fileName, currentCloudPath);
            }
        });
        if (last) {
            ctx.writeAndFlush(fileContentMessage);
            randomAccessFile.close();
//            NetworkController.send(new StorageMessage(currentCloudPath));
        }
    }
}
