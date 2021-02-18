package org.rainbow.dingtalk.sdk.message;

import lombok.Data;
import lombok.experimental.Accessors;
import org.rainbow.core.markdown.MarkdownBuilder;
import org.rainbow.dingtalk.sdk.DingTalkParams;
import org.rainbow.dingtalk.sdk.enums.MessageTypeEnum;

/**
 * @author K
 * @date 2021/2/18  14:34
 */
@Data
@Accessors(chain = true)
public class DingTalkMarkDownMessage extends AbstractDingTalkMessage {
    private String title;
    private MarkdownBuilder text;

    @Override
    public MessageTypeEnum getType() {
        return MessageTypeEnum.MARKDOWN;
    }

    @Override
    public DingTalkParams put(DingTalkParams params) {
        return params.setMarkdown(new DingTalkParams.Markdown().setTitle(this.title).setText(this.text.build()));
    }
}
