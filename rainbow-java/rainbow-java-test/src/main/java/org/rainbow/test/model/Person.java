package org.rainbow.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rainbow.elasticsearch.annotation.EsField;
import org.rainbow.elasticsearch.enums.EsAnalyzerType;
import org.rainbow.elasticsearch.enums.EsFieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author K
 * @date 2021/3/17  14:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {
    private static final long serialVersionUID = 8510634155374943623L;

    /**
     * 主键
     */
    @EsField(type = EsFieldType.KEYWORD)
    private Long id;

    /**
     * 名字
     */
    @EsField(type = EsFieldType.TEXT, analyzer = EsAnalyzerType.IK_MAX_WORD)
    private String name;

    /**
     * 国家
     */
    @EsField(type = EsFieldType.KEYWORD)
    private String country;

    /**
     * 年龄
     */
    @EsField(type = EsFieldType.INTEGER)
    private Integer age;

    /**
     * 生日
     */
    @EsField(type = EsFieldType.DATE)
    private Date birthday;

    /**
     * 介绍
     */
    @EsField(type = EsFieldType.KEYWORD)
    private String remark;
}
