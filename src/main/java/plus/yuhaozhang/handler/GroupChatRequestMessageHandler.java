package plus.yuhaozhang.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import plus.yuhaozhang.message.ChatResponseMessage;
import plus.yuhaozhang.message.GroupChatRequestMessage;
import plus.yuhaozhang.message.GroupChatResponseMessage;
import plus.yuhaozhang.server.Session.GroupSessionFactory;

import java.util.List;

/**
 * @author Yuh Z
 * @date 1/15/22
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupChatRequestMessage groupChatRequestMessage) throws Exception {
        String groupName = groupChatRequestMessage.getGroupName();
        String content = groupChatRequestMessage.getContent();
        String from = groupChatRequestMessage.getFrom();
        List<Channel> membersChannel = GroupSessionFactory.getGroupSession().getMembersChannel(groupName);
        if(membersChannel.size()==0){
            channelHandlerContext.writeAndFlush(new GroupChatResponseMessage(false,"没有该群组"));
        }else {
            for (Channel channel : membersChannel) {
                channel.writeAndFlush(new GroupChatResponseMessage(from, content));
            }
        }
    }
}
