package org.rainbow.notice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 针对异步方法无法捕获到异常的情况使用的注解
 * @author K
 * @date 2021/2/22  18:37
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ExceptionNotice {

}
