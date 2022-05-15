package ru.gb.storage.commons.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.netty.channel.ChannelHandlerContext;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        property = "type"
)
public abstract class Message {

//    public abstract void handle(ChannelHandlerContext ctx) throws Exception;

}

