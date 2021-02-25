package org.rainbow.websocket.support;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.rainbow.websocket.annotation.RequestParam;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.core.MethodParameter;

import java.util.List;
import java.util.Map;

import static org.rainbow.websocket.pojo.PojoEndpointServer.REQUEST_PARAM;

/**
 * 注解了@RequestParam的方法参数解析器
 *
 * @author K
 * @date 2021/2/25  10:29
 */
public class RequestParamMethodArgumentResolver implements MethodArgumentResolver {

    private AbstractBeanFactory beanFactory;

    public RequestParamMethodArgumentResolver(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 判断方法参数是否有@RequestParam注解
     *
     * @param parameter 方法参数
     * @return true / false
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestParam.class);
    }

    /**
     * 参数解析
     *
     * @param parameter 方法参数
     * @param channel   通道
     * @param object    object对象
     * @return 解析后的对象
     * @throws Exception 异常
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        RequestParam annotation = parameter.getParameterAnnotation(RequestParam.class);
        String name = annotation.name();
        if (name.isEmpty()) {
            name = parameter.getParameterName();
            if (name == null) {
                throw new IllegalArgumentException(
                        "Name for argument type [" + parameter.getNestedParameterType().getName() +
                                "] not available, and parameter name information not found in class file either.");
            }
        }

        if (!channel.hasAttr(REQUEST_PARAM)) {
            QueryStringDecoder decoder = new QueryStringDecoder(((FullHttpRequest) object).uri());
            channel.attr(REQUEST_PARAM).set(decoder.parameters());
        }

        Map<String, List<String>> requestParams = channel.attr(REQUEST_PARAM).get();
        List<String> arg = requestParams != null ? requestParams.get(name) : null;
        TypeConverter typeConverter = beanFactory.getTypeConverter();
        if (arg == null) {
            if ("\\n\\t\\t\\n\\t\\t\\n\\uE000\\uE001\\uE002\\n\\t\\t\\t\\t\\n".equals(annotation.defaultValue())) {
                return null;
            } else {
                return typeConverter.convertIfNecessary(annotation, parameter.getParameterType());
            }
        }

        if (List.class.isAssignableFrom(parameter.getParameterType())) {
            return typeConverter.convertIfNecessary(arg, parameter.getParameterType());
        } else {
            return typeConverter.convertIfNecessary(arg.get(0), parameter.getParameterType());
        }
    }
}
