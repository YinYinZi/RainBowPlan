package org.rainbow.core.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.rainbow.core.base.BaseEnum;

import java.io.IOException;

/**
 * 自定义枚举序列化实现
 *
 * @author K
 * @date 2021/2/5  9:50
 */
public class EnumSerializer extends StdSerializer<BaseEnum> {
    public static final EnumSerializer INSTANCE = new EnumSerializer();
    public static final String ALL_ENUM_KEY_FIELD = "code";
    public static final String ALL_ENUM_DESC_FIELD = "desc";

    public EnumSerializer() {
        super(BaseEnum.class);
    }

    @Override
    public void serialize(BaseEnum distance, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        generator.writeFieldName("code");
        generator.writeString(distance.getCode());
        generator.writeFieldName("desc");
        generator.writeString(distance.getCode());
        generator.writeEndObject();
    }
}
