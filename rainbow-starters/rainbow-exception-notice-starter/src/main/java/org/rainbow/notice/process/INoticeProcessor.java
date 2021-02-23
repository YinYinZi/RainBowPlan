package org.rainbow.notice.process;

import org.rainbow.notice.content.ExceptionInfo;

/**
 * 异常信息通知处理接口
 *
 * @author K
 * @date 2021/2/23  10:28
 */
public interface INoticeProcessor {

    /**
     * 异常信息通知
     *
     * @param exceptionInfo 异常信息
     */
    void sendNotice(ExceptionInfo exceptionInfo);
}
