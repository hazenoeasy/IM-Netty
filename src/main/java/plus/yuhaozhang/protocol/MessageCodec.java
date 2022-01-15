package plus.yuhaozhang.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import plus.yuhaozhang.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author Yuh Z
 * @date 1/12/22
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magic = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializeType = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();
        byteBuf.readByte(); //  读空
        int length = byteBuf.readInt();
        byte[] content = new byte[length];
        byteBuf.readBytes(content,0,length);
        if(serializeType == 0){
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(content));
            Message message = (Message) inputStream.readObject();
            log.debug("receive: {}", message);
            list.add(message);
        }

    }

    @Override
    public void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        log.debug("send: {}",message);
        // 写入magic 数
        byteBuf.writeBytes(Message.magic);
        // 版本号
        byteBuf.writeByte(1);
        // 序列化方式
        byteBuf.writeByte(0);
        // 指令
        byteBuf.writeByte(message.getMessageType());
        // 写入序列号
        byteBuf.writeInt(message.getSequenceId());
        //对其填充
        byteBuf.writeByte(0xff);
        //序列化
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(message);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        //写入 内容长度
        byteBuf.writeInt(byteArray.length);
        // 写入内容
        byteBuf.writeBytes(byteArray);
    }
}
