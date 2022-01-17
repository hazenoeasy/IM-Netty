package plus.yuhaozhang.server.service;

/**
 * @author Yuh Z
 * @date 1/16/22
 */
public class HelloServiceImpl2 implements HelloService {
    @Override
    public String sayHello(String name) {
        int i = 1 / 0;
        return "hello2 " + name;
    }
}
