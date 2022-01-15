package plus.yuhaozhang.server.Session;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class SessionFactory {

    private static String group = "group1";

    private static Set<String> set= new HashSet<String>(Arrays.asList(new String[]{"zhangsan","lisi"}));
    private static Session session = new SessionMemoryImpl();

    public static Session getSession() {
        return session;
    }
}
