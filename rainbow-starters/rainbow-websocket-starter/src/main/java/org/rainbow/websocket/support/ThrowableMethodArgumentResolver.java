package org.rainbow.websocket.support;

import io.netty.channel.Channel;
import org.rainbow.websocket.annotation.OnError;
import org.springframework.core.MethodParameter;

/**
 * Throwable类型的方法参数解析器
 *
 * @author K
 * @date 2021/2/25  9:58
 */
public class ThrowableMethodArgumentResolver implements MethodArgumentResolver {

    /**
     * 判断方法参数对应的方法是否注解了OnError && 方法参数类型是否与Throwable类型一致或其子类
     *
     * @param methodParameter 方法参数
     * @return true / false
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getMethod().isAnnotationPresent(OnError.class)
                && Throwable.class.isAssignableFrom(methodParameter.getParameterType());
    }

    /**
     * Throwable类型的方法参数解析器
     *
     * @param parameter 方法参数
     * @param channel   通道
     * @param object    object对象
     * @return Throwable类型对象
     * @throws Exception 异常
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        if (object instanceof Throwable) {
            return object;
        }
        return null;
    }
}
