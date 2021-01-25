package org.rainbow.core.exception;

/**
 * 非业务异常
 * 用于在处理非业务逻辑时抛出的异常
 *
 * @author K
 * @date 2021/1/24  13:36
 */
public class CommonException extends BaseCheckedException {

    public CommonException(int code, String message) {
        super(code, message);
    }

    public CommonException(int code, String format, Object... args) {
        super(code, String.format(format, args));
        this.code = code;
        this.message = String.format(format, args);
    }

    public CommonException wrap(int code, String format, Object... args) {
        return new CommonException(code, format, args);
    }

    @Override
    public String toString() {
        return "BizException [message=" + message + ", code=" + code + "]";
    }
}
