package org.rainbow.elasticsearch.enums;

import lombok.Getter;

/**
 * @author K
 * @date 2021/3/18  15:11
 */
@Getter
public enum EsFieldType {

    /**
     * text
     */
    TEXT("text"),

    /**
     * keyword
     */
    KEYWORD("keyword"),

    /**
     * integer
     */
    INTEGER("integer"),

    /**
     * double
     */
    DOUBLE("double"),

    /**
     * date
     */
    DATE("date"),

    /**
     * 单条数据
     */
    OBJECT("object"),

    /**
     * 嵌套数组
     */
    NESTED("nested"),
    ;

    EsFieldType(String type) {
        this.type = type;
    }

    private final String type;
}
