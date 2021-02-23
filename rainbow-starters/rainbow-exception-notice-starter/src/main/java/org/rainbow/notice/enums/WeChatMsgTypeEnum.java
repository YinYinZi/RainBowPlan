package org.rainbow.notice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 企业微信消息类型枚举
 *
 * @author K
 * @date 2021/2/22  18:17
 */
@Getter
@RequiredArgsConstructor
public enum WeChatMsgTypeEnum {

    /**
     * 文本类型
     */
    TEXT("text"),

    /**
     * MARKDOWN
     */
    MARKDOWN("markdown");

    private final String msgType;
}
