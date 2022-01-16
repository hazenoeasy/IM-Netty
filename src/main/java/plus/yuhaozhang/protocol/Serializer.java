package plus.yuhaozhang.protocol;

import com.google.gson.*;

import java.lang.reflect.Type;
/**
 * @author Yuh Z
 * @date 1/14/22
 */

/**
 * 用于扩展序列化、反序列化算法
 */
public interface Serializer {

    /**
     * 表示传入什么类型，就返回什么类型
     * @param clazz 反序列化的类型
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    /**
     *
     * @param object
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T object);

}