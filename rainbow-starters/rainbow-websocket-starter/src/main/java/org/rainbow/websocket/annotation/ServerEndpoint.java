package org.rainbow.websocket.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务器端点
 *
 * @author K
 * @date 2021/2/24  11:02
 */
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServerEndpoint {

    @AliasFor("path")
    String path() default "/";

    @AliasFor("value")
    String value() default "/";

    String host() default "0.0.0.0";

    String port() default "80";

    String bossLoopGroupThreads() default "1";

    String workerLoopGroupThreads() default "0";

    String useCompressionHandler() default "false";

    //------------------------- option -------------------------

    String optionConnectionTimeoutMillis() default "30000";

    String optionSoBacklog() default "128";

    //------------------------- childOption -------------------------

    String childOptionWriteSpinCount() default "16";

    String childOptionWriteBufferHighWaterMark() default "65536";

    String childOptionWriteBufferLowWaterMark() default "32768";

    String childOptionSoRcvbuf() default "-1";

    String childOptionSoSndbuf() default "-1";

    String childOptionTcpNodelay() default "true";

    String childOptionSoKeepalive() default "false";

    String childOptionSoLinger() default "-1";

    String childOptionAllowHalfClosure() default "false";

    //------------------------- idleEvent -------------------------

    String readerIdleTimeSeconds() default "0";

    String writerIdleTimeSeconds() default "0";

    String allIdleTimeSeconds() default "0";

    //------------------------- handshake -------------------------

    String maxFramePayloadLength() default "65536";

    //------------------------- eventExecutorGroup -------------------------

    String useEventExecutorGroup() default "true";

    String eventExecutorGroupThread() default "16";

    //------------------------- ssl (refer to spring Ssl) -------------------------

    String sslKeyPassword() default "";

    String sslKeyStore() default "";

    String sslKeyStorePassword() default "";

    String sslKeyStoreType() default "";

    String sslTrustStore() default "";

    String sslTrustStorePassword() default "";

    String sslTrustStoreType() default "";

    //------------------------- cors (refer to spring CrossOrigin) -------------------------
    String[] corsOrigins() default {};

    String corsAllowCredentials() default "";
}
