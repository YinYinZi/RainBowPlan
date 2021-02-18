package org.rainbow.dingtalk.sdk.message;

import lombok.Data;
import lombok.experimental.Accessors;
import org.rainbow.dingtalk.sdk.DingTalkParams;
import org.rainbow.dingtalk.sdk.enums.MessageTypeEnum;

/**
 * @author K
 * @date 2021/2/18  14:36
 */
@Data
@Accessors(chain = true)
public class DingTalkTextMessage extends AbstractDingTalkMessage {
    private String content;

    @Override
    public MessageTypeEnum getType() {
        return MessageTypeEnum.TEXT;
    }

    @Override
    public DingTalkParams put(DingTalkParams params) {
        return params.setText(new DingTalkParams.Text().setContent(this.content));
    }
}
