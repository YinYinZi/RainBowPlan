package org.rainbow.activemq.spring;

import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author K
 * @date 2021/1/20  16:51
 */
@Service
public class DemoMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;

        try {
            System.out.println(textMessage.getText());
            // ....
            // 执行成功后, 确认消息
            message.acknowledge();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
