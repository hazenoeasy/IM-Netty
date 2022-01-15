package plus.yuhaozhang.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import plus.yuhaozhang.message.LoginRequestMessage;
import plus.yuhaozhang.message.LoginResponseMessage;
import plus.yuhaozhang.server.Session.SessionFactory;
import plus.yuhaozhang.server.service.UserServiceFactory;

/**
 * @author Yuh Z
 * @date 1/15/22
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                LoginRequestMessage loginRequestMessage) throws Exception {
        String username = loginRequestMessage.getUsername();
        String password = loginRequestMessage.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        if (login) {
            SessionFactory.getSession().bind(channelHandlerContext.channel(),username);
            channelHandlerContext.writeAndFlush(new LoginResponseMessage(login, "登录成功"));
        } else {
            channelHandlerContext.writeAndFlush(new LoginResponseMessage(false, "登录失败"));
        }
    }
}
