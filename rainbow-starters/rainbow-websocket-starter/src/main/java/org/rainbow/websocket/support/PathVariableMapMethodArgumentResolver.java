package org.rainbow.websocket.support;

import cn.hutool.core.collection.CollectionUtil;
import io.netty.channel.Channel;
import org.rainbow.websocket.annotation.PathVariable;
import org.springframework.core.MethodParameter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;

import static org.rainbow.websocket.pojo.PojoEndpointServer.URI_TEMPLATE;

/**
 * 注解了@PathVariable的方法上Map类型的方法参数解析器
 *
 * @author K
 * @date 2021/2/25  10:53
 */
public class PathVariableMapMethodArgumentResolver implements MethodArgumentResolver {

    /**
     * 判断方法是否标注了@PathVariable并且方法参数类型是否为Map类型
     *
     * @param parameter 方法参数
     * @return true / false
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        PathVariable annotation = parameter.getParameterAnnotation(PathVariable.class);
        return (annotation != null && Map.class.isAssignableFrom(parameter.getParameterType())
                && !StringUtils.hasText(annotation.name()));
    }

    /**
     * 将方法参数解析成Map类型
     *
     * @param parameter 方法参数
     * @param channel   通道
     * @param object    object对象
     * @return 解析后的map类型对象
     * @throws Exception 异常
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        PathVariable annotation = parameter.getParameterAnnotation(PathVariable.class);
        String name = annotation.name();
        if (name.isEmpty()) {
            name = parameter.getParameterName();
            if (name == null) {
                throw new IllegalArgumentException(
                        "Name for argument type [" + parameter.getNestedParameterType().getName() +
                                "] not available, and parameter name information not found in class file either.");
            }
        }
        Map<String, String> uriTemplateVars = channel.attr(URI_TEMPLATE).get();
        if (!CollectionUtils.isEmpty(uriTemplateVars)) {
            return uriTemplateVars;
        } else {
            return Collections.emptyMap();
        }
    }
}
