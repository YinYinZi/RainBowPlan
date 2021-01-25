package org.rainbow.core.exception.code;

/**
 * @author K
 * @date 2021/1/24  13:28
 */
public interface BaseExceptionCode {
    /**
     * 异常编码
     *
     * @return
     */
    int getCode();

    /**
     * 异常消息
     *
     * @return
     */
    String getMsg();
}
