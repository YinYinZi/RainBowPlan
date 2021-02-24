package org.rainbow.notice.properties;

import lombok.Data;
import org.rainbow.notice.enums.DingTalkMsgTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 钉钉机器人配置属性
 *
 * @author K
 * @date 2021/2/22  18:13
 */
@Data
public class DingTalkProperties {

    /**
     * 钉钉机器人webHook地址
     */
    private String webHook;

    /**
     * 钉钉机器人加签
     */
    private String sign;

    /**
     * 发送消息时被@的钉钉用户手机号
     */
    private String[] atMobiles;

    /**
     * 是否@群里所有人
     */
    private Boolean isAtAll = false;

    /**
     * 消息类型
     */
    private DingTalkMsgTypeEnum msgType = DingTalkMsgTypeEnum.TEXT;
}
