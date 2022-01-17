package plus.yuhaozhang.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import plus.yuhaozhang.handler.RpcRequestMessageHandler;
import plus.yuhaozhang.protocol.MessageCodecSharable;
import plus.yuhaozhang.protocol.ProtocolFrameDecoder;

/**
 * @author Yuh Z
 * @date 1/15/22
 */
@Slf4j
public class RpcServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        RpcRequestMessageHandler rpcRequestMessageHandler = new RpcRequestMessageHandler();

        try {
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ProtocolFrameDecoder());
                    socketChannel.pipeline().addLast(loggingHandler);
                    socketChannel.pipeline().addLast(messageCodecSharable);
                    socketChannel.pipeline().addLast(rpcRequestMessageHandler);
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            log.debug("Exception: {}", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
