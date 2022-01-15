package plus.yuhaozhang.message;

import lombok.Data;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuh Z
 * @date 1/12/22
 */
@Data
public abstract class Message implements Serializable {

    //public static Class<?> getMessageClass(int messageType){}
    private int sequenceId;
    private int messageType;

    /**
     * sdfs
     * @return sdf
     */
    public abstract int getMessageType();
    public static final byte[] magic = new byte[]{1,2,3,4};
    public static final int LoginRequestMessage = 0;
    public static final int LoginResponseMessage = 1;
    public static final int ChatRequestMessage = 2;
    public static final int ChatResponseMessage = 3;
    public static final int GroupCreateRequestMessage = 4;
    public static final int GroupCreateResponseMessage = 5;
    public static final int GroupJoinRequestMessage = 6;
    public static final int GroupJoinResponseMessage = 7;
    public static final int GroupQuitRequestMessage = 8;
    public static final int GroupQuitResponseMessage = 9;
    public static final int GroupChatRequestMessage = 10;
    public static final int GroupChatResponseMessage = 11;
    public static final int GroupMembersRequestMessage = 12;
    public static final int GroupMembersResponseMessage = 13;
    public static final int PingMessage = 14;
    public static final int PongMessage = 15;
    private static final Map<Integer,Class<?>> message = new HashMap<>();
}
