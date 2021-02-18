package org.rainbow.dingtalk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rainbow.dingtalk.properties.DingTalkProperties;
import org.rainbow.dingtalk.sdk.DingTalkSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author K
 * @date 2021/2/18  14:51
 */
@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ DingTalkProperties.class })
public class DingTalkAutoConfiguration {

    private final DingTalkProperties dingTalkProperties;

    @Bean
    @ConditionalOnMissingBean
    public DingTalkSender dingTalkSender() {
        try {
            return new DingTalkSender(dingTalkProperties.getUrl()).setSecret(dingTalkProperties.getSecret());
        } catch (Exception e) {
            log.error("初始化DingTalk失败：{}", e.getMessage());
        }
        return null;
    }
}
