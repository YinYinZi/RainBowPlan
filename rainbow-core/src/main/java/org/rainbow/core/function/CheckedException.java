package org.rainbow.core.function;


/**
 * 处理异常的函数
 *
 * @author K
 * @FunctionalInterface，主要用于编译级错误检查，加上该注解，当你写的接口不符合函数式接口定义的时候，编译器会报错。
 * @date 2021/1/25  15:15
 */
@FunctionalInterface
public interface CheckedException<T, R> {


    /**
     * 执行
     *
     * @param t 入参
     * @return R 出参
     * @throws Exception 异常
     */
    R apply(T t) throws Exception;
}
