package org.rainbow.rabbitmq.producer.sender;

import org.rainbow.rabbitmq.entity.Order;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author K
 * @date 2021/1/23  10:58
 */
@Component
public class OrderSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sned(Order order) {
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(order.getMessageId());
        rabbitTemplate.convertAndSend("order-exchange", "order.test", order, correlationData);
    }
}
