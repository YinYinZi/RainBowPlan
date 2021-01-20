package org.rainbow.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author K
 * @date 2021/1/20  16:10
 */
public class Consumer {

    public static void main(String[] args) throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://47.111.248.7:61616");
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("TEST.QUEUE");
        MessageProducer producer = session.createProducer(queue);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        for (int i = 0; i < 100; i++) {
            TextMessage message = session.createTextMessage("hello world!" + i);
            producer.send(message);
            System.out.println(message);
        }
        producer.close();
    }
}
