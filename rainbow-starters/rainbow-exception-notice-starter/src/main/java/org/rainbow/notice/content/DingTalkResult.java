package org.rainbow.notice.content;

import lombok.Data;

/**
 * 钉钉异常通知响应结果
 *
 * @author K
 * @date 2021/2/23  10:25
 */
@Data
public class DingTalkResult {
    private int errCode;

    private String errMsg;

}
