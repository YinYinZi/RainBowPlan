package org.rainbow.websocket.support;

import io.netty.channel.Channel;
import org.rainbow.websocket.annotation.PathVariable;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.core.MethodParameter;

import java.util.Map;

import static org.rainbow.websocket.pojo.PojoEndpointServer.URI_TEMPLATE;

/**
 * 处理注解了@PathVariable的方法的参数解析器
 *
 * @author K
 * @date 2021/2/25  11:04
 */
public class PathVariableMethodArgumentResolver implements MethodArgumentResolver {

    private AbstractBeanFactory beanFactory;

    public PathVariableMethodArgumentResolver(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 判断方法参数是否有@PathVaribale注解
     *
     * @param parameter 方法参数
     * @return true / false
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PathVariable.class);
    }

    /**
     * 解析方法参数
     *
     * @param parameter 方法参数
     * @param channel   通道
     * @param object    object对象
     * @return 对应参数的对象
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
        Object arg = (uriTemplateVars != null ? uriTemplateVars.get(name) : null);
        TypeConverter typeConverter = beanFactory.getTypeConverter();
        return typeConverter.convertIfNecessary(arg, parameter.getParameterType());
    }
}
