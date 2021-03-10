package org.rainbow.websocket.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 允许web socket
 *
 * @author K
 * @date 2021/2/24  10:30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({NettyWebSocketSelector.class})
public @interface EnableWebSocket {

}
