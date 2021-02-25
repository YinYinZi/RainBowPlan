package org.rainbow.websocket.annotation;

import cn.hutool.core.annotation.Alias;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路径变量
 *
 * @author K
 * @date 2021/2/24  10:51
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PathVariable {

    @Alias("value")
    String name() default "";

    @Alias("name")
    String value() default "";
}
