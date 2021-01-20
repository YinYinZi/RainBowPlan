package org.rainbow.rocketmq.spring.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ属性
 *
 * @author K
 * @date 2021/1/19  9:54
 */
@SuppressWarnings("weakerAccess")
@ConfigurationProperties(prefix = "spring.rocketmq")
@Data
public class RocketMQProperties {

    /**
     * RocketMQ Name Server 格式：host:port;host:port
     */
    private String nameServer;

    private Producer producer;

    @Data
    public static class Producer {

        /**
         * producer name
         */
        private String group;

        /**
         * 发送消息超时时间 单位毫秒
         */
        private int sendMsgTimeout = 3000;

        /**
         * 消息体压缩阀值 超过4k将被压缩
         */
        private int compressMsgBodyOverHostMuch = 1024 * 4;

        /**
         * 发送失败的重试次数
         */
        private int retryTimesWhenSendFailed = 2;

        /**
         * 异步模式发送失败的重试次数
         */
        private int retryTimesWhenSendAsyncFailed = 2;

        /**
         *  内部发送失败后尝试其他代理
         */
        private boolean retryAnotherBrokerWhenNotStoreOk = false;

        /**
         * 消息允许的最大字节数 4M
         */
        private int maxMessageSize = 1024*1024 * 4;
    }
}
