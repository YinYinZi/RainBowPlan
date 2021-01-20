package org.rainbow.rocketmq.spring.starter.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.rainbow.rocketmq.spring.starter.enums.MessageDelayLevel;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author K
 * @date 2021/1/19  16:29
 */
@SuppressWarnings({"WekerAccess", "unused"})
@Slf4j
public class RocketMQTemplate extends AbstractMessageSendingTemplate<String> implements InitializingBean, DisposableBean {

    @Setter
    @Getter
    private DefaultMQProducer producer;

    @Setter
    @Getter
    private ObjectMapper objectMapper = new ObjectMapper();

    @Setter
    @Getter
    private MessageQueueSelector messageQueueSelector;

    /**
     * 同步发送
     *
     * @param destination 格式：`topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @return {@link org.apache.rocketmq.client.producer.SendResult}
     */
    public SendResult syncSend(String destination, Message<?> message) {
        return syncSend(destination, message, producer.getSendMsgTimeout());
    }

    /**
     * 同步发送 并且指定超时时间
     *
     * @param destination 格式：`topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @param timeout     发送超时时间
     * @return {@link org.apache.rocketmq.client.producer.SendResult}
     */
    public SendResult syncSend(String destination, Message<?> message, long timeout) {
        if (Objects.isNull(message)) {
            log.info("syncSend failed. destination: {}, message is null", destination);
            throw new IllegalArgumentException("`message` and `message.payload` cannot be null");
        } else {
            message.getPayload();
        }

        try {
            long now = System.currentTimeMillis();
            org.apache.rocketmq.common.message.Message rocketMsg = convertToRocketMsg(destination, message);
            SendResult sendResult = producer.send(rocketMsg, timeout);
            long costTime = System.currentTimeMillis() - now;
            log.debug("send message cost: {} ms, msgId:{}", costTime, sendResult.getMsgId());
            return sendResult;
        } catch (Exception e) {
            log.info("SyncSend failed. destination:{}, message:{}", destination, message);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    /**
     * 将Spring Message转化成RocketMQ Message
     *
     * @param destination 格式：`topicName:tags`
     * @param message     {@link org.springframework.messaging.Message}
     * @return {@link org.springframework.messaging.Message}的实现
     */
    private org.apache.rocketmq.common.message.Message convertToRocketMsg(String destination, Message<?> message) {
        Object payloadObj = message.getPayload();
        byte[] payloads;

        if (isPrimitiveType(payloadObj)) {
            payloads = payloadObj.toString().getBytes(StandardCharsets.UTF_8);
        } else {
            try {
                String jsonObj = this.objectMapper.writeValueAsString(payloadObj);
                payloads = jsonObj.getBytes(StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("convert to RocketMQ message failed.", e);
            }
        }

        String[] tempArr = destination.split(":", 2);
        String topic = tempArr[0];
        String tags = "";
        if (tempArr.length > 1) {
            tags = tempArr[1];
        }

        org.apache.rocketmq.common.message.Message rocketMsg
                = new org.apache.rocketmq.common.message.Message(topic, tags, payloads);

        MessageHeaders headers = message.getHeaders();
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            Object keys = headers.get(MessageConst.PROPERTY_KEYS);
            if (!StringUtils.isEmpty(keys)) {
                rocketMsg.setKeys(keys.toString());
            }

            Object flagObj = headers.getOrDefault("FLAG", "0");
            int flag = 0;
            try {
                flag = Integer.parseInt(flagObj.toString());
            } catch (NumberFormatException e) {
                log.info("flag must be integer, flagObj:{}", flagObj);
            }
            rocketMsg.setFlag(flag);

            Object waitStoreMsgOkObj = headers.getOrDefault("WAIT_STORE_MSG_OK", "true");
            boolean waitStoreMsgOk = Boolean.TRUE.equals(waitStoreMsgOkObj);
            rocketMsg.setWaitStoreMsgOK(waitStoreMsgOk);

            headers.entrySet().stream()
                    .filter(entry -> !Objects.equals(entry.getKey(), MessageConst.PROPERTY_KEYS)
                            && !Objects.equals(entry.getKey(), "FLAG")
                            && !Objects.equals(entry.getKey(), "WAIT_STORE_MSG_OK"))
                    .forEach(entry -> {
                        rocketMsg.putUserProperty("USERS_" + entry.getKey(), String.valueOf(entry.getValue()));
                    });
        }
        return rocketMsg;
    }

    /**
     * 同步发送
     *
     * @param destination 格式：`topicName:tags`
     * @param payload     负载对象
     * @return {@link org.apache.rocketmq.client.producer.SendResult}
     */
    public SendResult syncSend(String destination, Object payload) {
        return syncSend(destination, payload, producer.getSendMsgTimeout());
    }

    /**
     * 同步发送
     *
     * @param destination 格式 `topicName:tags`
     * @param payload     负载对象
     * @param timeout     发送超时时间
     * @return {@link org.apache.rocketmq.client.producer.SendResult}
     */
    public SendResult syncSend(String destination, Object payload, long timeout) {
        Message<?> message = this.doConvert(payload, null, null);
        return syncSend(destination, message, timeout);
    }

    /**
     * 同步发送
     *
     * @param destination 格式：`topicName:tags`
     * @param payload     负载对象
     * @param keys        用于消息hash索引，方便查询，尽可能唯一
     * @return {@link org.apache.rocketmq.client.producer.SendResult}
     */
    public SendResult syncSend(String destination, Object payload, String keys) {
        Message<?> message = this.doConvert(payload, keys);
        return syncSend(destination, payload, producer.getSendMsgTimeout(), keys);
    }

    /**
     * 同步发送
     *
     * @param destination 格式：`topicName:tags`
     * @param payload     负载对象
     * @param timeout     发送超时时间
     * @param keys        用于消息hash索引，方便查询，尽可能唯一
     * @return {@link org.apache.rocketmq.client.producer.SendResult}
     */
    public SendResult syncSend(String destination, Object payload, long timeout, String keys) {
        Message<?> message = this.doConvert(payload, keys);
        return syncSend(destination, payload, timeout);
    }

    /**
     * 延迟发送
     *
     * @param destination       格式：`topicName:tags`
     * @param payload           负载对象
     * @param keys              用于消息hash索引，方便查询，尽可能唯一
     * @param delayLevel {@link org.rainbow.rocketmq.spring.starter.enums.MessageDelayLevel}
     * @return {@link org.apache.rocketmq.client.producer.SendResult}
     */
    public SendResult sendDelayed(String destination, Object payload, String keys, MessageDelayLevel delayLevel) {
        Message<?> message = this.doConvert(payload, keys);
        return sendDelayed(destination, payload, delayLevel);
    }

    /**
     * 延迟发送
     *
     * @param destination       格式：`topicName:tags`
     * @param payload           负载对象
     * @param delayLevel 消息延迟级别 {@link org.rainbow.rocketmq.spring.starter.enums.MessageDelayLevel}
     * @return {@link org.apache.rocketmq.client.producer.SendResult}
     */
    public SendResult sendDelayed(String destination, Object payload, MessageDelayLevel delayLevel) {
        Message<?> message = this.doConvert(payload, null, null);
        // TODO:
        return null;
    }



    /**
     * 将payload转化为Message
     *
     * @param payload 负载对象
     * @param keys    headers key
     * @return {@link org.springframework.messaging.Message}
     */
    private Message<?> doConvert(Object payload, String keys) {
        HashMap<String, Object> headers = new HashMap<>();
        headers.put(MessageConst.PROPERTY_KEYS, keys);
        Message<?> message = this.doConvert(payload, headers, null);
        return message;
    }

    /**
     * 判断对象是否为原始类型
     *
     * @param obj {@link java.lang.Object}
     * @return true / false
     */
    private boolean isPrimitiveType(Object obj) {
        return obj instanceof String || obj instanceof Byte || obj instanceof Short
                || obj instanceof Integer || obj instanceof Long || obj instanceof Float
                || obj instanceof Double || obj instanceof Boolean;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    protected void doSend(String s, Message<?> message) {

    }
}
