package org.rainbow.activemq.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author K
 * @date 2021/1/20  16:51
 */
public class Consumer {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring/activemq-consumer.xml");
    }
}
