package plus.yuhaozhang.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import plus.yuhaozhang.message.RpcResponseMessage;

/**
 * @author Yuh Z
 * @date 1/15/22
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends  SimpleChannelInboundHandler<RpcResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponseMessage rpcResponseMessage) {
        try {
            Exception exceptionValue = rpcResponseMessage.getExceptionValue();
            Object returnValue = rpcResponseMessage.getReturnValue();
            if (exceptionValue != null) {
                log.debug("encounter an Exception: {}", exceptionValue);
            } else {
                log.debug("hello: {}", returnValue);
            }
        } catch (Exception e) {
            log.debug("Exception: {}", e);
        }
    }
}
