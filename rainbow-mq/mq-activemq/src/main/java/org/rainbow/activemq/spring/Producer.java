package org.rainbow.activemq.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author K
 * @date 2021/1/20  16:51
 */
public class Producer {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context
                = new ClassPathXmlApplicationContext("spring/activemq-producer.xml");
        JmsTemplate defaultProducer = (JmsTemplate) context.getBean("defaultMessageProducer");
        defaultProducer.convertAndSend("Hello Active");
    }
}
