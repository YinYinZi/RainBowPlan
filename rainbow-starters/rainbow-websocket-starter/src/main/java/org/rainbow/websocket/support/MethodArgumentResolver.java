package org.rainbow.websocket.support;

import io.netty.channel.Channel;
import org.springframework.core.MethodParameter;

/**
 * 用于将方法参数解析成参数的策略接口
 *
 * @author K
 * @date 2021/2/24  9:42
 */
public interface MethodArgumentResolver {

    /**
     * 判断此解析器是否支持给定的方法参数
     *
     * @param parameter 方法参数
     * @return true / false
     */
    boolean supportsParameter(MethodParameter parameter);

    /**
     * 将给定方法参数解析成参数
     *
     * @param parameter 方法参数
     * @param channel 通道
     * @param object object对象
     * @return 解析后的对象
     * @throws Exception 异常
     */
    Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception;
}
