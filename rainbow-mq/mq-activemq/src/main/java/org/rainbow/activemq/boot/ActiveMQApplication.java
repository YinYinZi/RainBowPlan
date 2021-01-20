package org.rainbow.activemq.boot;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Queue;


/**
 * @author K
 * @date 2021/1/20  16:39
 */
@EnableJms
@SpringBootApplication
public class ActiveMQApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActiveMQApplication.class);
    }

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("sample.queue");
    }
}
