package org.rainbow.rocketmq.spring.starter.enums;

/**
 * 消费模式
 *
 * @author K
 * @date 2021/1/20  14:49
 */
public enum ConsumeMode {

    /**
     * 同时接收异步传递的消息
     */
    CONCURRENTLY,

    /**
     * 顺序接收异步传递的消息
     */
    ORDERLY
}
