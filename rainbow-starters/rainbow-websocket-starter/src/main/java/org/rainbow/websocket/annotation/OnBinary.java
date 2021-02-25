package org.rainbow.websocket.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当接收到二进制消息时，对该方法进行回调 注入参数的类型:Session、byte[]
 *
 * @author K
 * @date 2021/2/24  10:33
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnBinary {
}
