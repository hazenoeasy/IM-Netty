package plus.yuhaozhang.server.service;

/**
 * @author Yuh Z
 * @date 1/14/22
 */

/**
 * 用户管理接口
 */
public interface UserService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回true
     */
    boolean login(String username, String password);
}
