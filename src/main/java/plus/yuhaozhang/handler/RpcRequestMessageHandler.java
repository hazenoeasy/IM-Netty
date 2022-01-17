package plus.yuhaozhang.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import plus.yuhaozhang.message.RpcRequestMessage;
import plus.yuhaozhang.message.RpcResponseMessage;
import plus.yuhaozhang.server.service.HelloService;
import plus.yuhaozhang.server.service.ServicesFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Yuh Z
 * @date 1/15/22
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequestMessage rpcRequestMessage) {
        RpcResponseMessage responseMessage = new RpcResponseMessage();
        responseMessage.setSequenceId(rpcRequestMessage.getSequenceId());
        try {
            Object service = ServicesFactory.getService(Class.forName(rpcRequestMessage.getInterfaceName()));
            Method method = service.getClass().getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParameterTypes());
            Object invoke = method.invoke(service, rpcRequestMessage.getParameterValue());
            responseMessage.setReturnValue(invoke);
        } catch (Exception e) {
            String message = e.getCause().getMessage();
            System.out.println(message);
            responseMessage.setExceptionValue(new Exception("Rpc fail: " + message));
            System.out.println(responseMessage);
        }
        channelHandlerContext.writeAndFlush(responseMessage);
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(1,
                "plus.yuhaozhang.server.service.HelloService", "sayHello", String.class, new Class[]{String.class},
                new Object[]{"zhangsan"});
        HelloService service = (HelloService) ServicesFactory.getService(Class.forName(rpcRequestMessage.getInterfaceName()));
        Method method = service.getClass().getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParameterTypes());
        Object invoke = method.invoke(service, rpcRequestMessage.getParameterValue());
        System.out.println(invoke);
    }
}
