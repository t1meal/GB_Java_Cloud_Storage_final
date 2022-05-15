package ru.gb.storage.commons.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gb.storage.commons.messages.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class JSonDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final byte [] bytes = ByteBufUtil.getBytes(msg);
        Message message = OBJECT_MAPPER.readValue(bytes, Message.class);
        out.add(message);
    }
}