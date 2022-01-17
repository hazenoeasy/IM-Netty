package plus.yuhaozhang.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import plus.yuhaozhang.handler.RpcResponseBusHandler;
import plus.yuhaozhang.handler.RpcResponseMessageHandler;
import plus.yuhaozhang.message.RpcRequestMessage;
import plus.yuhaozhang.protocol.MessageCodecSharable;
import plus.yuhaozhang.protocol.ProtocolFrameDecoder;
import plus.yuhaozhang.protocol.SequenceIdGenerator;
import plus.yuhaozhang.server.service.HelloService;

import java.lang.reflect.Proxy;

/**
 * @author Yuh Z
 * @date 1/15/22
 */
@Slf4j
public class RpcClientManager {
    private static Channel channel = null;
    private static final Object LOCK = new Object();

    public static void main(String[] args) {
        HelloService service = getProxyService(HelloService.class);
        System.out.println(service.sayHello("nihao zhangsan"));

    }

    // 实现代理类
    private static <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader classLoader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        // 调用方法 就会进入代理对象 触发handler
        Object newProxyInstance = Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
            // handler 负责发送
            RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(SequenceIdGenerator.nextId(),
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args);

            // 指定promise 异步对象接收结果线程
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseBusHandler.PROMISE_MAP.put(rpcRequestMessage.getSequenceId(), promise);

            // 发送数据
            RpcClientManager.getChannel().writeAndFlush(rpcRequestMessage);

            // 等待promise结果
            promise.await();

            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                // 调用失败
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) newProxyInstance;
    }

    // 单例模式下获得通道
    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    private static void initChannel() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();
        RpcResponseBusHandler rpcResponseBusHandler = new RpcResponseBusHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(worker);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new ProtocolFrameDecoder());
                socketChannel.pipeline().addLast(loggingHandler);
                socketChannel.pipeline().addLast(messageCodecSharable);
                //socketChannel.pipeline().addLast(rpcResponseMessageHandler);
                socketChannel.pipeline().addLast(rpcResponseBusHandler);
            }
        });
        try {
            RpcClientManager.channel = bootstrap.connect("localhost", 8080).sync().channel();
            RpcClientManager.channel.closeFuture().addListener(future -> {
                worker.shutdownGracefully();
            });
        } catch (Exception e) {
            log.debug("Exception: {}", e);
        }
    }
}
