package org.rainbow.rabbitmq.consumer.receiver;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.rainbow.rabbitmq.entity.Order;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author K
 * @date 2021/1/23  11:52
 */
@Component
@Slf4j
public class OrderReceiver {

    /**
     * {@link org.springframework.amqp.rabbit.annotation.RabbitListener} 配置监听哪一个队列, 在没有queue和exchange的情况下会去创建并建立绑定关系
     * {@link org.springframework.amqp.rabbit.annotation.RabbitListener} 如果有消息过来, 在消费的时候会调用这个方法
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "order-queue", durable = "true"),
            exchange = @Exchange(name = "order-exchange", durable = "true", type = "topic"), key = "order.*"))
    @RabbitHandler
    public void onOrderMessage(@Payload Order order, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        //消费者操作
        log.info("---------收到消息，开始消费---------");
        log.info("订单ID: {}", order.getId());

         // DeliveryTag用来标志信道中投递的消息。RabbitMQ将消息推送给Consumer, 会附带一个Delivery Tag
         // 以便Consumer可以在消息确认的时候告诉RabbitMQ到底那条消息被确认了
         // RabbitMQ保证在每个信道中, 每条消息的Delivery tag从1开始递增
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        // multiple为false时, 表示通知RabbitMQ当前消息被确认
        // 如果为true 会额外把比当前消息的delivery tag小的消息一并确认
        boolean multiple = false;

        // ack, 确认一条消息已经被消费
        channel.basicAck(deliveryTag, multiple);
    }

}
