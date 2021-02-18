package org.rainbow.dingtalk.sdk.enums;

/**
 * 排版方式枚举
 *
 * @author K
 * @date 2021/2/18  13:34
 */
public enum ActionBtnOrientationEnum {
    /**
     * 按钮竖向排列
     */
    VERTICAL("0", "按钮竖向排列"),
    /**
     * 按钮横向排列
     */
    HORIZONTAL("1", "按钮横向排列");

    private final String val;
    private final String text;

    public String getVal() {
        return this.val;
    }

    public String getText() {
        return this.text;
    }

    private ActionBtnOrientationEnum(String val, String text) {
        this.val = val;
        this.text = text;
    }
}
