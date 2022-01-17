# 开发问题记录

* logback 需要配置多个logger，对库函数和自己写的包都要写logger 否者信息不全。
* 有的handler不能共享 
* https://blog.csdn.net/winfredzen/article/details/78458233 java 静态
* synchronized 加锁 防止单例模式多线调用 double check
* Java static 类不能用 this 因为 this 指向 实例, 在静态类中，可以使用类名.属性的方式指定内部属性
* 代理类 负责的转换接口
* `<T> T` 什么意思
* 为什么Java 代理基于接口实现：因为Java中不支持多继承，而JDK的动态代理在创建代理对象时，默认让代理对象继承了Proxy类，所以JDK只能通过接口去实现动态代理。
* Client 发送基于接口的RpcMessage, Server通过接口，在工厂类中从application.properties中找接口的实现类,并实例化实现类，并且调用方法
* Future 和 Promise 区别 https://zhuanlan.zhihu.com/p/144415625
* 泛型通配符 ? 只能取值，不能放值
* Java Serialize 库函数 编码长度巨长无比，如果想传递Exception 肯定会爆