package org.rainbow.elasticsearch.annotation;

import org.rainbow.elasticsearch.enums.EsAnalyzerType;
import org.rainbow.elasticsearch.enums.EsFieldType;

import java.lang.annotation.*;

/**
 * 作用在字段上，用于定义类型，映射关系
 *
 * @author K
 * @date 2021/3/18  15:09
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface EsField {

    EsFieldType type() default EsFieldType.TEXT;

    EsAnalyzerType analyzer() default EsAnalyzerType.STANDARD;

}
