package org.rainbow.notice.process;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.rainbow.notice.content.ExceptionInfo;
import org.rainbow.notice.content.WeChatExceptionInfo;
import org.rainbow.notice.properties.WeChatProperties;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * 企业微信异常信息通知具体实现
 *
 * @author K
 * @date 2021/2/23  10:56
 */
@Data
@Slf4j
public class WeChatNoticeProcessor implements INoticeProcessor {
    private final WeChatProperties weChatProperties;

    private final RestTemplate restTemplate;

    public WeChatNoticeProcessor(WeChatProperties weChatProperties, RestTemplate restTemplate) {
        Assert.hasText(weChatProperties.getWebHook(), "WeChat webhook must not be null");
        this.weChatProperties = weChatProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendNotice(ExceptionInfo exceptionInfo) {
        WeChatExceptionInfo weChatExceptionInfo = new WeChatExceptionInfo(exceptionInfo, weChatProperties);
        String result = restTemplate.postForObject(weChatProperties.getWebHook(), weChatExceptionInfo, String.class);
        log.debug(result);
    }
}
