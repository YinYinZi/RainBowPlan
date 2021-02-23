package org.rainbow.notice.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rainbow.notice.enums.WeChatMsgTypeEnum;
import org.rainbow.notice.properties.WeChatProperties;

/**
 * 企业微信异常通知消息请求体
 *
 * @author K
 * @date 2021/2/23  10:13
 */
@Data
public class WeChatExceptionInfo {

    private WeChatText text;
    private WeChatMarkDown markDown;
    private String msgType;

    public WeChatExceptionInfo(ExceptionInfo exceptionInfo, WeChatProperties weChatProperties) {
        WeChatMsgTypeEnum msgType = weChatProperties.getMsgType();
        if (msgType.equals(WeChatMsgTypeEnum.TEXT)) {
            this.text = new WeChatText(exceptionInfo.createText(), weChatProperties.getAtUserIds(), weChatProperties.getAtPhones()) ;
        } else if (msgType.equals(WeChatMsgTypeEnum.MARKDOWN)) {
            this.markDown = new WeChatMarkDown(exceptionInfo.createText());
        }
        this.msgType = msgType.getMsgType();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class WeChatText {
        private String content;

        private String[] mentionedList;

        private String[] mentionedMobileList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class WeChatMarkDown {
        private String content;
    }
}
