package org.rainbow.notice.config;

import org.rainbow.notice.aop.ExceptionListener;
import org.rainbow.notice.handler.ExceptionNoticeHandler;
import org.rainbow.notice.process.DingTalkNoticeProcessor;
import org.rainbow.notice.process.INoticeProcessor;
import org.rainbow.notice.process.MailNoticeProcessor;
import org.rainbow.notice.process.WeChatNoticeProcessor;
import org.rainbow.notice.properties.DingTalkProperties;
import org.rainbow.notice.properties.ExceptionNoticeProperties;
import org.rainbow.notice.properties.MailProperties;
import org.rainbow.notice.properties.WeChatProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 异常信息通知配置类
 *
 * @author K
 * @date 2021/2/23  11:45
 */
@Configuration
@ConditionalOnProperty(prefix = ExceptionNoticeProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(value = ExceptionNoticeProperties.class)
public class ExceptionNoticeAutoConfiguration {
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired(required = false)
    private MailSender mailSender;

    @Bean(initMethod = "start")
    public ExceptionNoticeHandler noticeHandler(ExceptionNoticeProperties properties) {
        List<INoticeProcessor> noticeProcessors = new ArrayList<>(2);
        INoticeProcessor noticeProcessor;
        DingTalkProperties dingTalkProperties = properties.getDingTalk();
        if (null != dingTalkProperties) {
            noticeProcessor = new DingTalkNoticeProcessor(dingTalkProperties, restTemplate);
            noticeProcessors.add(noticeProcessor);
        }
        WeChatProperties weChatProperties = properties.getWeChat();
        if (null != weChatProperties) {
            noticeProcessor = new WeChatNoticeProcessor(weChatProperties, restTemplate);
            noticeProcessors.add(noticeProcessor);
        }
        MailProperties email = properties.getEmail();
        if (null != email && null != mailSender) {
            noticeProcessor = new MailNoticeProcessor(email, mailSender);
            noticeProcessors.add(noticeProcessor);
        }
        Assert.isTrue(noticeProcessors.size() != 0, "Exception notification configuration is incorrect");
        return new ExceptionNoticeHandler(properties, noticeProcessors);
    }

    @Bean
    @ConditionalOnBean(ExceptionNoticeHandler.class)
    public ExceptionListener exceptionListener(ExceptionNoticeHandler noticeHandler) {
        return new ExceptionListener(noticeHandler);
    }
}

