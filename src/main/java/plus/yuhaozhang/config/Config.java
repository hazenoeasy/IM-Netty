package plus.yuhaozhang.config;

import plus.yuhaozhang.protocol.Serializer;
import plus.yuhaozhang.protocol.SerializerEnum;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Yuh Z
 * @date 1/14/22
 */
public abstract class Config {
    static Properties properties;

    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    public static int getServerPort() {
        String value = properties.getProperty("server.port");
        if(value == null) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }
    public static SerializerEnum getSerializerAlgorithm() {
        String value = properties.getProperty("serializer.algorithm");
        if(value == null) {
            return SerializerEnum.Java;
        } else {
            return SerializerEnum.valueOf(value);
        }
    }
}