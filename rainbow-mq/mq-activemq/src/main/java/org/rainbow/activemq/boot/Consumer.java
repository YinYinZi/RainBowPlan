package org.rainbow.activemq.boot;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * @author K
 * @date 2021/1/20  16:38
 */
@Component
public class Consumer {

    @JmsListener(destination = "sample.queue")
    public void receiveMsg(String text) {
        System.out.println(text);
    }
}
