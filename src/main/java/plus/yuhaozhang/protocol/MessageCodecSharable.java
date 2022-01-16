package plus.yuhaozhang.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import plus.yuhaozhang.config.Config;
import plus.yuhaozhang.message.Message;

import java.util.List;

/**
 * 必须和LengthFieldBasedFrameDecoder 一起使用，解决半包问题
 * @author Yuh Z
 * @date 1/14/22
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> list) {
        ByteBuf byteBuf = channelHandlerContext.alloc().buffer();
        log.debug("send: {}", message);
        // 写入magic 数
        byteBuf.writeBytes(Message.magic);
        // 版本号
        byteBuf.writeByte(1);
        // 序列化方式
        byteBuf.writeByte(Config.getSerializerAlgorithm().ordinal());
        // 指令
        byteBuf.writeByte(message.getMessageType());
        // 写入序列号
        byteBuf.writeInt(message.getSequenceId());
        //对其填充
        byteBuf.writeByte(0xff);
        //序列化
        byte[] serialize = Config.getSerializerAlgorithm().serialize(message);
        //写入 内容长度
        byteBuf.writeInt(serialize.length);
        // 写入内容
        byteBuf.writeBytes(serialize);
        list.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception{
        int magic = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializeType = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();
        byteBuf.readByte(); //  读空
        int length = byteBuf.readInt();
        byte[] content = new byte[length];
        byteBuf.readBytes(content, 0, length);
        SerializerEnum serializerEnum = SerializerEnum.values()[serializeType];
        Message message = serializerEnum.deserialize(Message.getMessageClass(messageType), content);
        log.debug("receive: {}", message);
        list.add(message);
    }
}

