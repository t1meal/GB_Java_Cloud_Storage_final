package ru.gb.storage.commons.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.gb.storage.commons.messages.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;


import java.util.List;

public class JSonEncoder extends MessageToMessageEncoder<Message> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        byte [] bytes = OBJECT_MAPPER.writeValueAsBytes(msg);
        out.add(ctx.alloc().buffer().writeBytes(bytes));
    }
}
