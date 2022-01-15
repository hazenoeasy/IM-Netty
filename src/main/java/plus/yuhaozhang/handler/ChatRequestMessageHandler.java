package plus.yuhaozhang.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import plus.yuhaozhang.message.ChatRequestMessage;
import plus.yuhaozhang.message.ChatResponseMessage;
import plus.yuhaozhang.server.Session.SessionFactory;

/**
 * @author Yuh Z
 * @date 1/15/22
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatRequestMessage chatRequestMessage) throws Exception {
        String to = chatRequestMessage.getTo();
        String messageContent = chatRequestMessage.getContent();
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (channel!=null){
            channel.writeAndFlush(new ChatResponseMessage(chatRequestMessage.getFrom(),chatRequestMessage.getContent()));
        }else{
            channelHandlerContext.writeAndFlush(new ChatResponseMessage(false,"对方不在线"));
        }
    }
}
