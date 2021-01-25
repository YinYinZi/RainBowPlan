package org.rainbow.core.log.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author K
 * @date 2021/1/25  16:16
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    /**
     * 是否启用操作日志
     */
    boolean enabled() default true;

    /**
     * 描述
     */
    String value() default "";

    /**
     * 记录执行参数
     */
    boolean request() default true;

    /**
     * 当 request = false时， 方法报错是否记录请求参数
     */
    boolean requestByError() default true;

    /**
     * 记录返回参数
     */
    boolean response() default true;
}
