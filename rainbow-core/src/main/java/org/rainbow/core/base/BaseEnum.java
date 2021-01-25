package org.rainbow.core.base;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.rainbow.core.utils.MapHelper;

import java.util.Arrays;
import java.util.Map;

/**
 * 枚举类型基类
 *
 * @author K
 * @date 2021/1/24  12:49
 */
public interface BaseEnum extends IEnum<String> {

    /**
     * 将制定的枚举集合转化为Map
     *
     * key -> name
     * value -> desc
     *
     * @param list 枚举列表
     * @return map
     */
    static Map<String, String> getMap(BaseEnum[] list) {
        return MapHelper.uniqueIndex(Arrays.asList(list), BaseEnum::getCode, BaseEnum::getDesc);
    }

    /**
     * 编码重写
     */
    default String getCode() {
        return toString();
    }

    /**
     * 描述
     */
    String getDesc();

    /**
     * 枚举值
     */
    @Override
    @JsonIgnore
    default String getValue() {
        return getCode();
    }
}
