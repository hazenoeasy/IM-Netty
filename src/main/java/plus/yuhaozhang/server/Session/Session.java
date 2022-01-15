package plus.yuhaozhang.server.Session;

import io.netty.channel.Channel;

/**
 * @author Yuh Z
 * @date 1/14/22
 */
public interface Session {

    /**
     * 绑定会话
     * @param channel
     * @param username
     */
    void bind(Channel channel,String username);

    /**
     * 解除绑定
     * @param channel
     */
    void unbind(Channel channel);

    /**
     * 获取设置属性
     * @param channel
     * @param name
     * @return
     */
    Object getAttribute(Channel channel, String name);

    /**
     * 设置属性
     * @param channel
     * @param name
     * @param value
     */
    void setAttribute(Channel channel, String name, Object value);
    /**
     * 获取用户绑定的通道
     * @param username
     * @return
     */
    Channel getChannel(String username);


}
