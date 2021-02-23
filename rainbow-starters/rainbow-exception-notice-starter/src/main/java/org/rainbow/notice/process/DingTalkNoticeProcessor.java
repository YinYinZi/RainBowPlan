package org.rainbow.notice.process;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.rainbow.notice.content.DingTalkExceptionInfo;
import org.rainbow.notice.content.DingTalkResult;
import org.rainbow.notice.content.ExceptionInfo;
import org.rainbow.notice.properties.DingTalkProperties;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * 钉钉异常信息通知具体实现
 *
 * @author K
 * @date 2021/2/23  10:30
 */
@Data
@Slf4j
public class DingTalkNoticeProcessor implements INoticeProcessor {
    private final DingTalkProperties dingTalkProperties;

    private final RestTemplate restTemplate;

    public DingTalkNoticeProcessor(DingTalkProperties dingTalkProperties, RestTemplate restTemplate) {
        Assert.hasText(dingTalkProperties.getWebHook(), "DingTalk webhook must not be null");
        this.dingTalkProperties = dingTalkProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendNotice(ExceptionInfo exceptionInfo) {
        DingTalkExceptionInfo dingTalkExceptionInfo = new DingTalkExceptionInfo(exceptionInfo, dingTalkProperties);
        DingTalkResult result = restTemplate.postForObject(dingTalkProperties.getWebHook(), dingTalkExceptionInfo, DingTalkResult.class);
        log.debug(String.valueOf(result));
    }

}
