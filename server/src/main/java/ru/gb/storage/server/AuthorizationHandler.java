package ru.gb.storage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.storage.commons.messages.AuthorizationMessage;

public class AuthorizationHandler extends SimpleChannelInboundHandler<AuthorizationMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuthorizationMessage msg) {
        System.out.println("New authorization message!");
        msg.setAuthorizationStatus(msg.getLogin().equals("login1") && msg.getPassword().equals("pass1"));
        ctx.writeAndFlush(msg);
    }
}
