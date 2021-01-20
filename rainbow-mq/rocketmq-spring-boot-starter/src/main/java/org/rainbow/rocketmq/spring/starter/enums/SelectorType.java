package org.rainbow.rocketmq.spring.starter.enums;

/**
 * 选择器类型
 *
 * @author K
 * @date 2021/1/20  14:47
 */
public enum SelectorType {

    /**
     * @see org.apache.rocketmq.common.filter.ExpressionType#TAG
     */
    TAG,

    /**
     * @see org.apache.rocketmq.common.filter.ExpressionType#SQL92
     */
    SQL92
}
