package plus.yuhaozhang.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import plus.yuhaozhang.message.ChatResponseMessage;
import plus.yuhaozhang.message.GroupCreateRequestMessage;
import plus.yuhaozhang.message.GroupCreateResponseMessage;
import plus.yuhaozhang.server.Session.Group;
import plus.yuhaozhang.server.Session.GroupSessionFactory;

import java.util.List;
import java.util.Set;

/**
 * @author Yuh Z
 * @date 1/15/22
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupCreateRequestMessage groupCreateRequestMessage) throws Exception {
        String groupName = groupCreateRequestMessage.getGroupName();
        Set<String> members = groupCreateRequestMessage.getMembers();
        Group group = GroupSessionFactory.getGroupSession().createGroup(groupName, members);
        ChatResponseMessage chatResponseMessage;
        if (group == null) {
            List<Channel> membersChannel = GroupSessionFactory.getGroupSession().getMembersChannel(groupName);
            for (Channel channel : membersChannel) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true,"您已被拉入"+groupName+""));
            }
        } else {
            chatResponseMessage = new ChatResponseMessage(false, "创建失败");
            channelHandlerContext.writeAndFlush(chatResponseMessage);
        }
    }
}
