package plus.yuhaozhang.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Yuh Z
 * @date 1/13/22
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(callSuper = true)
public class LoginRequestMessage extends Message {
    private String username;
    private String password;

    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }
}
