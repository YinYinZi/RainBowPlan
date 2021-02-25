package org.rainbow.websocket.support;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.rainbow.websocket.annotation.RequestParam;
import org.springframework.core.MethodParameter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static org.rainbow.websocket.pojo.PojoEndpointServer.REQUEST_PARAM;

/**
 * 注解了@RequestParam的并且类型为Map方法参数的类型解析器
 *
 * @author K
 * @date 2021/2/25  10:13
 */
public class RequestParamMapMethodArgumentResolver implements MethodArgumentResolver {

    /**
     * 判断方法参数是否注解了@RequestParam && 方法参数类型是否为Map类型
     *
     * @param parameter 方法参数
     * @return true / false
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
        return (requestParam != null && Map.class.isAssignableFrom(parameter.getParameterType()) &&
                !StringUtils.hasText(requestParam.name()));
    }

    /**
     * 方法参数解析
     *
     * @param parameter 方法参数
     * @param channel   通道
     * @param object    object对象
     * @return Map类型对象
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
        MultiValueMap multiValueMap = new LinkedMultiValueMap(requestParams);
        if (MultiValueMap.class.isAssignableFrom(parameter.getParameterType())) {
            return multiValueMap;
        } else {
            return multiValueMap.toSingleValueMap();
        }
    }
}
