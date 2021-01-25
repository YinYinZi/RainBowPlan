package org.rainbow.core.exception;

/**
 * 非运行期异常基类, 所有自定义非运行期异常都继承此类
 *
 * @author K
 * @date 2021/1/24  13:34
 */
public class BaseUncheckedException extends RuntimeException implements BaseException {
    private static final long serialVersionUID = -778887391066124051L;

    /**
     * 异常信息
     */
    protected String message;

    /**
     * 具体异常码
     */
    protected int code;

    public BaseUncheckedException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseUncheckedException(int code, String format, Object... args) {
        super(String.format(format, args));
        this.code = code;
        this.message = String.format(format, args);
    }


    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getCode() {
        return code;
    }
}
