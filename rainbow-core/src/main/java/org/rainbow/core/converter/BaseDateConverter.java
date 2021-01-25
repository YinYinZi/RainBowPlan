package org.rainbow.core.converter;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 解决入参为 Date类型
 *
 * @author K
 * @date 2021/1/25  13:58
 */
public abstract class BaseDateConverter<T> {

    public T convert(String source, Function<String, T> function) {
        if (null == source || source.isEmpty()) {
            return null;
        }
        source = source.trim();
        Set<Map.Entry<String, String>> entries = getFormat().entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (source.matches(entry.getValue())) {
                return function.apply(entry.getKey());
            }
        }
        throw new IllegalArgumentException("无效的日期参数格式:'" + source + "'");
    }

    /**
     * 获取子类 具体的格式化表达式
     */
    protected abstract Map<String, String> getFormat();

}
