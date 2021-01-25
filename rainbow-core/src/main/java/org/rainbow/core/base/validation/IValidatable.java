package org.rainbow.core.base.validation;

/**
 * 实现此接口 代表此类将会支持验证框架
 *
 * @author K
 * @date 2021/1/25  11:47
 */
public interface IValidatable {

    /**
     * 此类需要检验什么值
     */
    Object value();
}
