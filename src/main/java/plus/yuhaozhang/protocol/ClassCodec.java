package plus.yuhaozhang.protocol;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author Yuh Z
 * @date 1/15/22
 */
class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

    @Override
    public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            String str = json.getAsString();
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    @Override             //   String.class
    public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
        // class -> json
        return new JsonPrimitive(src.getName());
    }
}
