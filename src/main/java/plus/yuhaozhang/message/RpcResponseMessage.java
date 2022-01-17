package plus.yuhaozhang.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Yuh Z
 * @date 1/15/22
 */
@ToString(callSuper = true)
@Data
public class RpcResponseMessage extends Message{
    /**
     *  返回值
     */
    private Object returnValue;

    /**
     * 异常值
     */
    private Exception exceptionValue;
    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
