package plus.yuhaozhang.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yuh Z
 * @date 1/14/22
 */
public class UserServiceMemoryImpl implements UserService {
    private Map<String, String> allUserMap = new ConcurrentHashMap<>();

    {
        allUserMap.put("zhangsan", "123");
        allUserMap.put("lisi", "123");
    }

    @Override
    public boolean login(String username, String password) {
        String pass = allUserMap.get(username);
        if (pass == null) {
            return false;
        }
        return pass.equals(password);
    }

}
