package org.rainbow.websocket.support;

import io.netty.channel.Channel;
import org.rainbow.websocket.annotation.OnEvent;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.core.MethodParameter;

import java.util.Objects;

/**
 * 注解了@OnEnvnet方法的方法参数对象解析器
 *
 * @author K
 * @date 2021/2/25  11:20
 */
public class EventMethodArgumentResolver implements MethodArgumentResolver {

    /**
     * Spring Bean Factory
     */
    private AbstractBeanFactory beanFactory;

    public EventMethodArgumentResolver(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 判断此方法参数是否注解了OnEvent注解
     *
     * @param parameter 方法参数
     * @return true / false
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getMethod().isAnnotationPresent(OnEvent.class);
    }

    /**
     * 将方法参数解析成对应的参数类型
     *
     * @param parameter 方法参数
     * @param channel   通道
     * @param object    object对象
     * @return 解析后的对象
     * @throws Exception 异常
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        if (Objects.isNull(object)) {
            return null;
        }
        TypeConverter typeConverter = beanFactory.getTypeConverter();
        return typeConverter.convertIfNecessary(object, parameter.getParameterType());
    }
}
