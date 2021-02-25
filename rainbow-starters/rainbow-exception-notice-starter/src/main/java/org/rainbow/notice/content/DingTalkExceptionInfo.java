package org.rainbow.notice.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.rainbow.notice.enums.DingTalkMsgTypeEnum;
import org.rainbow.notice.properties.DingTalkProperties;

/**
 * 钉钉异常通知消息请求体
 *
 * @author K
 * @date 2021/2/23  10:01
 */
@Data
@Slf4j
public class DingTalkExceptionInfo {

    private String msgtype;

    private DingTalkText text;

    private DingTalkMarkdown markdown;

    private DingTalkAt at;

    public DingTalkExceptionInfo(ExceptionInfo exceptionInfo, DingTalkProperties dingTalkProperties) {
        DingTalkMsgTypeEnum msgType = dingTalkProperties.getMsgType();
        if (msgType.equals(DingTalkMsgTypeEnum.MARKDOWN)) {
            this.markdown = new DingTalkMarkdown(exceptionInfo.getProject(), exceptionInfo.createDingTalkMarkDown());
        } else if (msgType.equals(DingTalkMsgTypeEnum.TEXT)) {
            this.text = new DingTalkText(exceptionInfo.createText());
        }
        this.msgtype = msgType.getMsgType();
        this.at = new DingTalkAt(dingTalkProperties.getAtMobiles(), dingTalkProperties.getIsAtAll());
    }

    @Data
    @AllArgsConstructor
    static class DingTalkText {
        private String content;
    }

    @Data
    @AllArgsConstructor
    static class DingTalkMarkdown {
        private String title;

        private String content;
    }

    @Data
    @AllArgsConstructor
    static class DingTalkAt {
        private String[] atMobiles;

        private Boolean isAtAll;
    }
}
