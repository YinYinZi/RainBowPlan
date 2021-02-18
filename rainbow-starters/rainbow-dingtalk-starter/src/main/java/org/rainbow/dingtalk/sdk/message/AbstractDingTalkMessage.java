package org.rainbow.dingtalk.sdk.message;

import org.rainbow.dingtalk.sdk.DingTalkParams;
import org.rainbow.dingtalk.sdk.enums.MessageTypeEnum;

import java.util.HashSet;
import java.util.Set;

/**
 * @author K
 * @date 2021/2/18  14:09
 */
public abstract class AbstractDingTalkMessage implements DingTalkMessage {
    private final Set<String> atPhones = new HashSet<>();
    private boolean atAll = false;

    public AbstractDingTalkMessage() {
    }

    public AbstractDingTalkMessage atAll() {
        this.atAll = true;
        return this;
    }

    public AbstractDingTalkMessage addPhone(String phone) {
        this.atPhones.add(phone);
        return this;
    }

    public abstract MessageTypeEnum getType();

    public abstract DingTalkParams put(DingTalkParams var1);

    @Override
    public String generate() {
        DingTalkParams params = this.put((new DingTalkParams()).setType(this.getType().getVal()).setAt((new DingTalkParams.At()).setAtAll(this.atAll).setAtMobiles(this.atPhones)));
        return params.toString();
    }
}
