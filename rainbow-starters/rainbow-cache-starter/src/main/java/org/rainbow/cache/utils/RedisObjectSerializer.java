package org.rainbow.cache.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.rainbow.core.jackson.JsonUtil;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * @author K
 * @date 2021/2/5  9:23
 */
public class RedisObjectSerializer extends Jackson2JsonRedisSerializer<Object> {


    public RedisObjectSerializer() {
        super(Object.class);
        ObjectMapper objectMapper = JsonUtil.getInstance();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        this.setObjectMapper(objectMapper);
    }
}
