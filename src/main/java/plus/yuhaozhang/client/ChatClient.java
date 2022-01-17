package plus.yuhaozhang.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import plus.yuhaozhang.message.*;
import plus.yuhaozhang.protocol.MessageCodecSharable;
import plus.yuhaozhang.protocol.ProtocolFrameDecoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Yuh Z
 * @date 1/14/22
 */
@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        final LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        final MessageCodecSharable messageCodec = new MessageCodecSharable();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000);
            bootstrap.group(eventLoopGroup);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ProtocolFrameDecoder());
                    //socketChannel.pipeline().addLast(loggingHandler);
                    socketChannel.pipeline().addLast(messageCodec);
                    socketChannel.pipeline().addLast(new IdleStateHandler(0, 10, 0));
                    // 读写双向handler
                    socketChannel.pipeline().addLast(new ChannelDuplexHandler() {
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;

                            //  用来触发特殊事件
                            if (event.state() == IdleState.WRITER_IDLE) {
                                //                                log.debug("3s 没有写数据了，发送一个心跳包");
                                ctx.writeAndFlush(new PingMessage());
                            }
                        }
                    });
                    socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            if(!(msg instanceof PingMessage)){
                                log.debug("read: {}", msg);
                            }
                            if(msg instanceof LoginResponseMessage){
                                if(((LoginResponseMessage)msg).isSuccess()){
                                    LOGIN.set(true);
                                }
                            }
                            WAIT_FOR_LOGIN.countDown();
                        }
                    });
                    socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 接收用户在控制台的输入，并向服务器发送消息
                            new Thread(() -> {
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("please type in user name");
                                String username = scanner.nextLine();
                                System.out.println("please type in password");
                                String password = scanner.nextLine();
                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username, password);
                                ctx.writeAndFlush(loginRequestMessage);
                                System.out.println("waiting...");
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if(!LOGIN.get()){
                                    System.out.println("login fail");
                                    ctx.close();
                                    return;
                                }
                                System.out.println("login success!");
                                while (true) {
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = null;
                                    try {
                                        command = scanner.nextLine();
                                    } catch (Exception e) {
                                        break;
                                    }
                                    String[] s = command.split(" ");
                                    switch (s[0]){
                                        case "send":
                                            System.out.println("send....");
                                            ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gcreate":
                                            Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            set.add(username); // 加入自己
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;
                                    }
                                }
                            }, "system in").start();
                        }
                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();
            Channel channel = channelFuture.channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.debug("Exception: {}", e);
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
