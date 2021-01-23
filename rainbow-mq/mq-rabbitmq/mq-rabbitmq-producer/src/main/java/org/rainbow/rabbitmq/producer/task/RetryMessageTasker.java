package org.rainbow.rabbitmq.producer.task;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.rainbow.rabbitmq.entity.BrokerMessageLog;
import org.rainbow.rabbitmq.entity.Order;
import org.rainbow.rabbitmq.producer.constant.Constants;
import org.rainbow.rabbitmq.producer.mapper.BrokerMessageLogMapper;
import org.rainbow.rabbitmq.producer.sender.RabbitOrderSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author K
 * @date 2021/1/23  11:22
 */
@Component
@Slf4j
public class RetryMessageTasker {

    @Autowired
    private RabbitOrderSender rabbitOrderSender;

    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;

    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
    public void reSend() {
        System.out.println("-----------定时任务开始-----------");
        List<BrokerMessageLog> list = brokerMessageLogMapper.query4StatusAndTimeoutMessage();
        list.forEach(messageLog -> {
            if (messageLog.getTryCount() > 3) {
                // 如果重试次数大于3 直接将消息置为失败
                brokerMessageLogMapper.changeBrokerMessageLogStatus(messageLog.getMessageId(),
                        Constants.ORDER_SEND_FAILURE, new Date());
            } else {
                brokerMessageLogMapper.update4ReSend(messageLog.getMessageId(),  new Date());
                Order reSendOrder = JSONUtil.toBean(messageLog.getMessage(), Order.class);
                try {
                    rabbitOrderSender.sendOrder(reSendOrder);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("重发消息失败 原因：{}", e.getMessage());
                }
            }
        });
    }
}
