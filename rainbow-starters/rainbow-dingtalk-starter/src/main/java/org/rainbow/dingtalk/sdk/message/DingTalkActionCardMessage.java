package org.rainbow.dingtalk.sdk.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.rainbow.core.markdown.MarkdownBuilder;
import org.rainbow.dingtalk.sdk.enums.ActionBtnOrientationEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @author K
 * @date 2021/2/18  14:17
 */
@Data
@Accessors
public class DingTalkActionCardMessage {
    private String title;
    private MarkdownBuilder text;
    private ActionBtnOrientationEnum orientation;
    private String singleTitle;
    private String singleUrl;
    private List<Button> buttons;

    public DingTalkActionCardMessage() {
        this.orientation = ActionBtnOrientationEnum.HORIZONTAL;
        this.buttons = new ArrayList();
    }



    @Data
    @AllArgsConstructor
    public static class Button {
        private final String title;
        private final String actionURL;
    }
}
