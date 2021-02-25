package org.rainbow.websocket.support;

import io.netty.channel.Channel;
import org.rainbow.websocket.pojo.Session;
import org.springframework.core.MethodParameter;

import static org.rainbow.websocket.pojo.PojoEndpointServer.SESSION_KEY;

/**
 * Session参数类型解析器
 *
 * @author K
 * @date 2021/2/25  9:44
 */
public class SessionMethodArgumentResolver implements MethodArgumentResolver {

    /**
     * 判断方法参数类型是否为Session
     *
     * @param methodParameter 方法参数
     * @return true / false
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return Session.class.isAssignableFrom(methodParameter.getParameterType());
    }

    /**
     * 将通道属性中的Session获取
     *
     * @param parameter 方法参数
     * @param channel   通道
     * @param object    object对象
     * @return Session
     * @throws Exception 异常
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        return channel.attr(SESSION_KEY).get();
    }
}
