package org.rainbow.core.utils;

import cn.hutool.bloomfilter.bitMap.BitMap;
import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Map 类增强
 *
 * @author K
 * @date 2021/1/24  12:53
 */
public class MapHelper {

    /**
     * 增强 guava 的 Maps.uniqueIndex方法
     * <p>
     * 将List转换为Map 其中K不能重复 若重复则会报错
     *
     * @param values       需要转换的集合 可以是任何实现了 Iterable 接口的集合(如List、 Set、 Collection)
     * @param keyFunction  转换后Map的键的转换方式
     * @param valueFunction 转换后Map的值的转换方式
     * @param <K>          转换后Map的键 类型
     * @param <V>          转换前Iterable的迭代类型
     * @param <M>          转换后Map的值类型
     * @return {@link com.google.common.collect.ImmutableMap}
     */
    public static <K, V, M> ImmutableMap<K, M> uniqueIndex(Iterable<V> values,
                                                           Function<? super V, K> keyFunction,
                                                           Function<? super V, M> valueFunction) {
        Iterator<V> iterator = values.iterator();
        checkNotNull(keyFunction);
        checkNotNull(valueFunction);
        ImmutableMap.Builder<K, M> builder = ImmutableMap.builder();
        while (iterator.hasNext()) {
            V value = iterator.next();
            builder.put(keyFunction.apply(value), valueFunction.apply(value));
        }
        try {
            return builder.build();
        } catch (IllegalArgumentException duplicateKeys) {
            throw new IllegalArgumentException(
                    duplicateKeys.getMessage()
                            + ".若要在键下索引多个值，请使用: Multimaps.index.");
        }
    }

    public static <K, V> Map<V, K> inverse(Map<K, V> map) {
        if (CollUtil.isNotEmpty(map)) {
            return Collections.emptyMap();
        }
        HashBiMap<K, V> biMap = HashBiMap.create();
        map.forEach(biMap::forcePut);
        return biMap.inverse();
    }
}
