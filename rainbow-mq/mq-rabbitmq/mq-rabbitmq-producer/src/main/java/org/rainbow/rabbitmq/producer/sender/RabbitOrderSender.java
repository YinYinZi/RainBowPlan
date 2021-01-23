package org.rainbow.rabbitmq.producer.sender;

import lombok.extern.slf4j.Slf4j;
import org.rainbow.rabbitmq.entity.Order;
import org.rainbow.rabbitmq.producer.constant.Constants;
import org.rainbow.rabbitmq.producer.mapper.BrokerMessageLogMapper;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author K
 * @date 2021/1/23  10:59
 */
@Component
@Slf4j
public class RabbitOrderSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;

    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            log.info("correlationData = {}", correlationData);
            String messageId = correlationData.getId();
            if (ack) {
                // 如果confirm发送成功 则更新消息状态为【发送成功】
                brokerMessageLogMapper.changeBrokerMessageLogStatus(messageId, Constants.ORDER_SEND_FAILURE, new Date());
            } else {
                // 失败则进行后续操作: 重试或补偿手段
                log.error("消息发送失败..");
            }
        }
    };

    /**
     * 发送消息调用：构建自定义消息
     */
    public void sendOrder(Order order) throws Exception {
        // 通过实现 ConfirmCallback 接口，消息发送到 Broker 后触发回调，
        // 确认消息是否到达 Broker 服务器，也就是只确认是否正确到达 Exchange 中
        rabbitTemplate.setConfirmCallback(confirmCallback);
        CorrelationData correlationData = new CorrelationData(order.getMessageId());
        rabbitTemplate.convertAndSend("order-exchange", "order.test", order, correlationData);
    }
}
