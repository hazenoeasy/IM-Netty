package plus.yuhaozhang.server.service;

/**
 * @author Yuh Z
 * @date 1/16/22
 */
public class HelloServiceImpl implements HelloService{
    @Override
    public String sayHello(String name) {
        return "hello "+name;
    }
}
