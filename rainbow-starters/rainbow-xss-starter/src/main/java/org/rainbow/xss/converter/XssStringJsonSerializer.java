package org.rainbow.xss.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.rainbow.xss.utils.XssUtils;

import java.io.IOException;

/**
 * 基于xss的json序列化器
 *
 * @author K
 * @date 2021/3/25  15:49
 */
@Slf4j
public class XssStringJsonSerializer extends JsonSerializer<String> {

    @Override
    public Class<String> handledType() {
        return String.class;
    }

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value != null) {
            try {
                String encodedValue = XssUtils.xssClean(value, null);
                jsonGenerator.writeString(encodedValue);
            } catch (Exception e) {
                log.error("序列化失败:[{}]", value, e);
            }
        }
    }
}
