package ru.gb.storage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import javafx.concurrent.Task;
import ru.gb.storage.commons.handlers.JSonDecoder;
import ru.gb.storage.commons.handlers.JSonEncoder;
import ru.gb.storage.commons.messages.Message;
import java.net.InetSocketAddress;
import java.util.Objects;

public class NetworkController {


        private final int PORT = 9000;
        private final String INET_HOST = "localhost";
        private static boolean connected;
        private static Channel channel;
        private NioEventLoopGroup group;




//      4. метод отправки сообщений серверу
        public static void send(Message message){
                if(!connected) {
                    return;
                }
                Task task = new Task() {
                    @Override
                    protected Void call() throws Exception {
                        ChannelFuture future = channel.writeAndFlush(message);
                        future.sync();
                        return null;
                    }
                    @Override
                    protected void failed() {
                        System.out.println("Неудачная попытка отправки сообщения!");
                    }
                };
                new Thread(task).start();
        }

//      1. первичный метод установления соединения с сервером
        public void connect() {
            if (connected){
                return;
            }
            group = new NioEventLoopGroup();
            Task<Channel> task = new Task<>() {
                @Override
                protected Channel call() throws Exception {

                    updateMessage("Bootstrapping");
                    updateProgress(0.1d, 1.0d);

                        Bootstrap bootstrap = new Bootstrap()
                                .group(group)
                                .channel(NioSocketChannel.class)
                                .remoteAddress(new InetSocketAddress(INET_HOST, PORT))
                                .option(ChannelOption.SO_KEEPALIVE, true)
                                .handler(new ChannelInitializer<SocketChannel>() {
                                    @Override
                                    protected void initChannel(SocketChannel ch) {
                                        ch.pipeline().addLast(
                                                new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                                new LengthFieldPrepender(3),
                                                new JSonDecoder(),
                                                new JSonEncoder(),
                                                new AuthorizationHandlerClient(),
                                                ClientHandler.getInstance()
                                        );
                                    }
                                });

                        System.out.println("Client started");
//                        Channel channel1 = bootstrap.connect(INET_HOST, PORT).sync().channel();
//                        channel1.closeFuture().sync();
//                        channel1.writeAndFlush(new TextMessage());
                        ChannelFuture future = bootstrap.connect();
                        future.sync();
                        Channel chn = future.channel();

                        updateMessage("Connecting");
                        updateProgress(0.2d, 1.0d);
                    return chn;
                }
                @Override
               protected void succeeded() {
                    channel = getValue();
                    connected = true;
                }
                @Override
                protected void failed() {
                    connected = false;
                }
            };
            new Thread(task).start();
        }

//  2. метод дисконекта подключения.
    public void disconnect() {
            if (!connected){
                return;
            }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Disconnecting");
                updateProgress(0.1d, 1.0d);

                channel.close().sync();
                updateMessage("Closing group");
                updateProgress(0.5d, 1.0d);

                group.shutdownGracefully().sync();
                connected = false;
                return null;
            }
        };
        new Thread(task).start();
    }
}
