package plus.yuhaozhang.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Yuh Z
 * @date 1/15/22
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(callSuper = true)
public class RpcRequestMessage extends Message {
    /**
     * 接口名
     */
    private String interfaceName;
    /**
     * 调用接口的方法
     */
    private String methodName;
    /**
     * 返回类型
     */
    private Class<?> returnType;

    /**
     * 参数类型数组
     */
    private Class[] parameterTypes;
    /**
     * 参数值数组
     */
    private Object[] parameterValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
    public RpcRequestMessage(int sequenceId, String interfaceName, String methodName, Class<?> returnType, Class[] parameterTypes, Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }
}
