package org.rainbow.database.mybatis.conditions;

import cn.hutool.core.util.ReflectUtil;
import org.rainbow.database.mybatis.conditions.query.LbqWrapper;
import org.rainbow.database.mybatis.conditions.query.QueryWrap;
import org.rainbow.database.mybatis.conditions.update.LbuWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Wrappers 工具类， 该方法的主要目的是为了 缩短代码长度
 *
 * @author K
 * @date 2021/1/26  14:05
 */
public class Wraps {

    private Wraps() {
        // ignore
    }

    /**
     * 获取 QueryWrap&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return QueryWrapper&lt;T&gt;
     */
    public static <T> QueryWrap<T> q() {
        return new QueryWrap<>();
    }

    /**
     * 获取 QueryWrap&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return QueryWrapper&lt;T&gt;
     */
    public static <T> QueryWrap<T> q(T entity) {
        return new QueryWrap<>(entity);
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return LambdaQueryWrapper&lt;T&gt;
     */
    public static <T> LbqWrapper<T> lbQ() {
        return new LbqWrapper<>();
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaQueryWrapper&lt;T&gt;
     */
    public static <T> LbqWrapper<T> lbQ(T entity) {
        return new LbqWrapper<>(entity);
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return LambdaUpdateWrapper&lt;T&gt;
     */
    public static <T> LbuWrapper<T> lbU() {
        return new LbuWrapper<>();
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaUpdateWrapper&lt;T&gt;
     */
    public static <T> LbuWrapper<T> lbU(T entity) {
        return new LbuWrapper<>(entity);
    }

    /**
     * 替换 实体对象中类型为String的参数 并将%和_符号转义
     *
     * @param source 源对象
     * @param <T>    最新源对象
     * @see
     */
    public static <T> T replace(Object source) {
        if (Objects.isNull(source)) {
            return null;
        }
        Object target = source;

        Class<?> srcClass = source.getClass();
        Field[] fields = ReflectUtil.getFields(srcClass);
        for (Field field : fields) {
            Object fieldValue = ReflectUtil.getFieldValue(source, field);
            if (Objects.isNull(field)) {
                continue;
            }

            // 跳过static 和 final 字段
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            // 非 String字段跳过
            if (!(fieldValue instanceof String)) {
                continue;
            }

            String strValue = (String) fieldValue;
            if (strValue.contains("%") || strValue.contains("_")) {
                String targetValue = strValue.replaceAll("%", "\\\\%");
                targetValue = targetValue.replaceAll("_", "\\\\_");
                // 将值回设
                ReflectUtil.setFieldValue(target, field, targetValue);
            }
        }
        return (T) target;
    }
}
