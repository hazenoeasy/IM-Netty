package plus.yuhaozhang.message;
/**
 * @author Yuh Z
 * @date 1/14/22
 */
public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return PongMessage;
    }
}
