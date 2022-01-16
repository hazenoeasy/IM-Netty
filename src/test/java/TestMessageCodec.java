import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import plus.yuhaozhang.message.LoginRequestMessage;
import plus.yuhaozhang.protocol.MessageCodec;

/**
 * @author Yuh Z
 * @date 1/12/22
 */
@Slf4j
public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new MessageCodec(),
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0));
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("zhangsan", "lisi");
        // encode
        embeddedChannel.writeOutbound(loginRequestMessage);
        // decode
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null,loginRequestMessage,byteBuf);

        embeddedChannel.writeInbound(byteBuf);
    }
}
