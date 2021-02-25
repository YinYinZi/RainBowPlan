package org.rainbow.websocket.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当有WebSocket抛出异常时，对该方法进行回调 注入参数的类型:Session、Throwable
 *
 * @author K
 * @date 2021/2/24  10:37
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnError {
}
