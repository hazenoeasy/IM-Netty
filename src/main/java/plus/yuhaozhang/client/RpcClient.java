package plus.yuhaozhang.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import plus.yuhaozhang.handler.RpcResponseMessageHandler;
import plus.yuhaozhang.message.RpcRequestMessage;
import plus.yuhaozhang.message.RpcResponseMessage;
import plus.yuhaozhang.protocol.MessageCodecSharable;
import plus.yuhaozhang.protocol.ProtocolFrameDecoder;

/**
 * @author Yuh Z
 * @date 1/15/22
 */
@Slf4j
public class RpcClient {
    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ProtocolFrameDecoder());
                    socketChannel.pipeline().addLast(loggingHandler);
                    socketChannel.pipeline().addLast(messageCodecSharable);
                    socketChannel.pipeline().addLast(rpcResponseMessageHandler);
                    socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(1,
                                    "plus.yuhaozhang.server.service.HelloService", "sayHello", String.class, new Class[]{String.class},
                                    new Object[]{"zhangsan"});
                            ctx.writeAndFlush(rpcRequestMessage);
                        }
                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.debug("Exception: {}", e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
