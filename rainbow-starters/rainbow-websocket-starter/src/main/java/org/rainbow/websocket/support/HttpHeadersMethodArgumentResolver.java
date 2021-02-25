package org.rainbow.websocket.support;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.core.MethodParameter;

/**
 * HttpHeaders类型的方法参数解析器
 *
 * @author K
 * @date 2021/2/25  9:51
 */
public class HttpHeadersMethodArgumentResolver implements MethodArgumentResolver {

    /**
     * 判断方法参数的参数类型是否与HttpHeaders类型一致或其子类
     *
     * @param methodParameter 方法参数
     * @return true / false
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return HttpHeaders.class.isAssignableFrom(methodParameter.getParameterType());
    }

    /**
     * 将方法参数解析为HttpHeader对象
     *
     * @param parameter 方法参数
     * @param channel   通道
     * @param object    object对象
     * @return 解析后的对象
     * @throws Exception 异常
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        return ((FullHttpRequest) object).headers();
    }
}
