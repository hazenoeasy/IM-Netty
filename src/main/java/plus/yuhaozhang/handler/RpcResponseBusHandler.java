package plus.yuhaozhang.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import plus.yuhaozhang.message.RpcResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yuh Z
 * @date 1/16/22
 */
@ChannelHandler.Sharable
public class RpcResponseBusHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    /**
     * Integer 是序号 Promise是接受结果
     */
    public static final Map<Integer, Promise<Object>> PROMISE_MAP = new ConcurrentHashMap<>();


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponseMessage rpcResponseMessage) throws Exception {
        int sequenceId = rpcResponseMessage.getSequenceId();
        Promise<Object> promise = PROMISE_MAP.remove(sequenceId);
        if(promise!=null){
            Object returnValue = rpcResponseMessage.getReturnValue();
            Exception exceptionValue = rpcResponseMessage.getExceptionValue();
            if(returnValue==null){
                promise.setFailure(exceptionValue);
            }else{
                promise.setSuccess(returnValue);
            }

        }
    }
}
