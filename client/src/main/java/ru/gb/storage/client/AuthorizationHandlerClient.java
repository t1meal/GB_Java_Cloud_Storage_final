package ru.gb.storage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.storage.commons.messages.AuthorizationMessage;

public class AuthorizationHandlerClient extends SimpleChannelInboundHandler <AuthorizationMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AuthorizationMessage message) {
        if (message.getAuthorizationStatus()){
            MainController.setIsAuthorized(true);
            System.out.println("Авторизация прошла успешно!");
        }
    }
}
