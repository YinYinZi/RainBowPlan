package org.rainbow.rabbitmq.producer.constant;

/**
 * @author K
 * @date 2021/1/23  9:38
 */
public class Constants {

    /**
     * 发送中
     */
    public static final String ORDER_SENDING = "0";

    /**
     * 发送成功
     */
    public static final String ORDER_SEND_SUCCESS = "1";

    /**
     * 发送失败
     */
    public static final String ORDER_SEND_FAILURE = "2";

    /**
     * 分钟超时单位：min
     */
    public static final int ORDER_TIMEOUT = 1;
}
