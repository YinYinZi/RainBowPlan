package org.rainbow.rabbitmq.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 服务消息日志
 *
 * @author K
 * @date 2021/1/21  15:19
 */
@Data
@Accessors(chain = true)
public class BrokerMessageLog {

    private String messageId;

    private String message;

    private Integer tryCount;

    private String status;

    private Date nextRetry;

    private Date createTime;

    private Date updateTime;

}
