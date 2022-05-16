package ru.gb.storage.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.storage.commons.messages.AuthorizationMessage;
import ru.gb.storage.server.services.AuthorizationService;

import java.sql.SQLException;

public class AuthorizationHandler extends SimpleChannelInboundHandler<AuthorizationMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuthorizationMessage msg) {
        System.out.println("New authorization message!");
        AuthorizationService authorization = new AuthorizationService();
        try {
            String authNick = authorization.checkUserInDB(msg.getLogin(), msg.getPassword());
            if (authNick != null) {
                System.out.println("User authorization success!");
                msg.setAuthorizationStatus(true);
                msg.setNick(authNick);
                authorization.disconnectDB();
                ctx.writeAndFlush(msg);
            } else {
                System.out.println("Wrong login and/or password!");
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
