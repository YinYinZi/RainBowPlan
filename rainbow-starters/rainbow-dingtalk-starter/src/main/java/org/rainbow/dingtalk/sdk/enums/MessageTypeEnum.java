package org.rainbow.dingtalk.sdk.enums;

import lombok.experimental.Accessors;

/**
 * 钉钉消息类型枚举
 *
 * @author K
 * @date 2021/2/18  13:30
 */
public enum MessageTypeEnum {
    /**
     * 文本
     */
    TEXT("text", "文本"),
    /**
     * 链接
     */
    LINK("link", "链接"),
    /**
     * markdown
     */
    MARKDOWN("markdown", "markdown"),
    /**
     * 跳转 actionCard 类型
     */
    ACTION_CARD("actionCard", "跳转 actionCard 类型");

    private final String val;
    private final String desc;

    public String getVal() {
        return val;
    }

    public String getDesc() {
        return desc;
    }

    MessageTypeEnum(String val, String desc) {
        this.val = val;
        this.desc = desc;
    }
}
