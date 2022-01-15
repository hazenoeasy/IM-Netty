package plus.yuhaozhang.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author Yuh Z
 * @date 1/14/22
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder{
    public ProtocolFrameDecoder() {
        super(1024, 12, 4, 0, 0);
    }

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
