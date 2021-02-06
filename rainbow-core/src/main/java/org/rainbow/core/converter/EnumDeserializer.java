package org.rainbow.core.converter;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * enum反序列化工具
 *
 * @author K
 * @date 2021/1/25  14:15
 */
@Slf4j
public class EnumDeserializer extends StdDeserializer<Enum<?>> {
    public static final EnumDeserializer INSTANCE = new EnumDeserializer();
    private static final String ALL_ENUM_STRING_CONVERT_METHOD = "get";
    private static final String ALL_ENUM_KEY_FIELD = "code";

    public EnumDeserializer() {
        super(Enum.class);
    }

    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonToken token = p.getCurrentToken();
        String value = null;
        while (!token.isStructEnd()) {
            if (ALL_ENUM_KEY_FIELD.equals(p.getText())) {
                p.nextToken();
                value = p.getValueAsString();
            } else {
                p.nextToken();
            }
            token = p.getCurrentToken();
        }
        if (null == value || "".equals(value)) {
            return null;
        }

        Object obj = p.getCurrentValue();
        if (null == obj) {
            return null;
        }
        Field field = ReflectUtil.getField(obj.getClass(), p.getCurrentName());
        if (null == field) {
            return null;
        }

        Class<?> fieldType = field.getType();
        try {
            Method method = fieldType.getMethod(ALL_ENUM_STRING_CONVERT_METHOD, String.class);
            return (Enum<?>) method.invoke(null, value);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            log.warn("解析枚举失败", e);
            return null;
        }
    }
}
