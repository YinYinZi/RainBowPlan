package org.rainbow.dingtalk.sdk.message;

import lombok.Data;
import lombok.experimental.Accessors;
import org.rainbow.dingtalk.sdk.DingTalkParams;
import org.rainbow.dingtalk.sdk.enums.MessageTypeEnum;

/**
 * @author K
 * @date 2021/2/18  14:28
 */
@Data
@Accessors(chain = true)
public class DingTalkLinkMessage extends AbstractDingTalkMessage {
    private String text;
    private String title;
    private String picUrl;
    private String messageUrl;

    @Override
    public MessageTypeEnum getType() {
        return MessageTypeEnum.LINK;
    }

    @Override
    public DingTalkParams put(DingTalkParams params) {
        return params.setLink(new DingTalkParams.Link().setTitle(this.title).setText(this.text)
                .setPicUrl(this.picUrl).setMessageUrl(this.messageUrl));
    }
}
