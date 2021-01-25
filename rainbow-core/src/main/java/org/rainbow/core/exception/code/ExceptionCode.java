package org.rainbow.core.exception.code;

/**
 * 自定义异常码
 *
 * @author K
 * @date 2021/1/24  13:39
 */
public class ExceptionCode implements BaseExceptionCode {

    ;

    private int code;
    private String msg;

    ExceptionCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }


    public ExceptionCode build(String msg, Object... param) {
        this.msg = String.format(msg, param);
        return this;
    }

    public ExceptionCode param(Object... param) {
        msg = String.format(msg, param);
        return this;
    }
}
