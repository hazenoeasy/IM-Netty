package plus.yuhaozhang.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import plus.yuhaozhang.handler.*;
import plus.yuhaozhang.message.PingMessage;
import plus.yuhaozhang.protocol.MessageCodecSharable;
import plus.yuhaozhang.protocol.ProtocolFrameDecoder;

/**
 * @author Yuh Z
 * @date 1/13/22
 */
@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup workers = new NioEventLoopGroup();
        final ChatRequestMessageHandler chatRequestMessageHandler = new ChatRequestMessageHandler();
        final LoginRequestMessageHandler loginRequestMessageHandler = new LoginRequestMessageHandler();
        final GroupCreateRequestMessageHandler groupCreateRequestMessageHandler =
                new GroupCreateRequestMessageHandler();
        final LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        final MessageCodecSharable messageCodec = new MessageCodecSharable();
        final GroupChatRequestMessageHandler groupChatRequestMessageHandler = new GroupChatRequestMessageHandler();
        final QuitHandler quitHandler = new QuitHandler();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .channel(NioServerSocketChannel.class)
                    .group(boss, workers)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
                                    .addLast(new ProtocolFrameDecoder());
                            socketChannel.pipeline().addLast(loggingHandler);
                            socketChannel.pipeline().addLast(messageCodec);
                            socketChannel.pipeline().addLast(new IdleStateHandler(100, 0, 0));
                            // ????????????handler
                            socketChannel.pipeline().addLast(new ChannelDuplexHandler() {
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                                    //  ????????????????????????
                                    if (evt instanceof IdleStateEvent) {
                                        IdleStateEvent event = (IdleStateEvent) evt;
                                        // ???????????????
                                        if (event.state() == IdleState.READER_IDLE) {
                                            ctx.channel().close();
                                        }
                                    }
                                }
                            });
                            socketChannel.pipeline().addLast(loginRequestMessageHandler);
                            socketChannel.pipeline().addLast(chatRequestMessageHandler);
                            socketChannel.pipeline().addLast(groupCreateRequestMessageHandler);
                            socketChannel.pipeline().addLast(groupChatRequestMessageHandler);
                            socketChannel.pipeline().addLast(quitHandler);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.debug("Exception: {}", e);
        } finally {
            boss.shutdownGracefully();
            workers.shutdownGracefully();
        }
    }

}
