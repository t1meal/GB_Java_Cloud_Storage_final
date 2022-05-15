package ru.gb.storage.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.storage.commons.FilesInfo;
import ru.gb.storage.commons.messages.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FirstServerHandler extends SimpleChannelInboundHandler<Message> {
    private final Path rootPath = Paths.get("server\\src\\main\\java\\resources");

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("New active channel");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        System.out.println("New message " + msg.getClass().toString());
        if (msg instanceof StorageMessage) {
            StorageMessage storageMessage = (StorageMessage) msg;
            if (storageMessage.getPath() == null){
                storageMessage.setList(fillingCloudList(rootPath));
                storageMessage.setPath(rootPath.toString());
            } else {
                Path updatePath = Paths.get(storageMessage.getPath());
                storageMessage.setList(fillingCloudList(updatePath));
            }
            ctx.writeAndFlush(storageMessage);
        }
        if (msg instanceof FileRequestMessage) {
            FileRequestMessage fileRequestMessage = (FileRequestMessage) msg;
            String fullPath = Paths.get(fileRequestMessage.getPath()).resolve(fileRequestMessage.getName()).toString();
            String filePath = fileRequestMessage.getPath();
            if (filePath != null) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(fullPath, "r");
                sendFile(randomAccessFile, ctx, fileRequestMessage.getName());
            }
        }
        if (msg instanceof FileContentMessage) {
            FileContentMessage fileContentMessage = (FileContentMessage) msg;
            String fullPath = rootPath.resolve(Paths.get(fileContentMessage.getName())).toString();
            RandomAccessFile accessFile = new RandomAccessFile(fullPath, "rw");
            System.out.println(fileContentMessage.getStartPosition());
            accessFile.seek(fileContentMessage.getStartPosition());
            accessFile.write(fileContentMessage.getContent());
        }
        if(msg instanceof FileSendMessage){
            FileSendMessage message = (FileSendMessage) msg;
            if(message.getPath() != null && message.getFileName() != null){
                ctx.writeAndFlush(message);
            }
        }
        if (msg instanceof FileDeleteMessage){
            FileDeleteMessage fileDeleteMessage = (FileDeleteMessage) msg;
            Path deletePath = fileDeleteMessage.getDeletePath();
            if (deletePath != null){
                deleteFile(deletePath);
                Path currentPath = deletePath.getParent();
                StorageMessage storageMessage = new StorageMessage(currentPath.toString());
                storageMessage.setList(fillingCloudList(currentPath));
                ctx.writeAndFlush(storageMessage);
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("New exception!");
        cause.printStackTrace();
        ctx.close();
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Client disconnect!");
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void sendFile(RandomAccessFile randomAccessFile, ChannelHandlerContext ctx, String fileName) throws IOException {
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
                sendFile(randomAccessFile, ctx, fileName);
            }
        });
        if (last) {
            randomAccessFile.close();
        }
    }
    private void deleteFile(Path path){
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private List<FilesInfo> fillingCloudList (Path path){
        File[] storageFiles = path.toFile().listFiles();
        List<FilesInfo> listFiles = new ArrayList<>();
        if (storageFiles != null){
            for (File file : storageFiles) {
                listFiles.add(new FilesInfo(file.toPath()));
            }
        }
        return listFiles;
    }
}

