package org.rainbow.websocket.support;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.rainbow.websocket.annotation.OnMessage;
import org.springframework.core.MethodParameter;

/**
 * 注解了OnMessage的String参数类型解析器
 *
 * @author K
 * @date 2021/2/25  9:54
 */
public class TextMethodArgumentResolver implements MethodArgumentResolver {

    /**
     * 判断该方法参数是否是String类型 && 该方法参数对应的方法是否注解了@OnMessage
     *
     * @param methodParameter 方法参数
     * @return true / false
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getMethod().isAnnotationPresent(OnMessage.class)
                && String.class.isAssignableFrom(methodParameter.getParameterType());
    }

    /**
     * 将方法参数转化为string
     *
     * @param parameter 方法参数
     * @param channel   通道
     * @param object    object对象
     * @return String类型的对象
     * @throws Exception 异常
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        return ((TextWebSocketFrame) object).text();
    }
}
