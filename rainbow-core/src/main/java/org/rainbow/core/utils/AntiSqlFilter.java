package org.rainbow.core.utils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL注入拦截器
 *
 * @author K
 * @date 2021/1/25  16:35
 */
public class AntiSqlFilter {

    private static final String[] KEY_WORDS = {";", "\"", "\'", "/*", "*/", "--", "exec",
            "select", "update", "delete", "insert", "alter", "drop", "create", "shutdown"};

    public static Map<String, String[]> getSafeParameterMap(Map<String, String[]> parameterMap) {
        Map<String, String[]> map = new HashMap<>(parameterMap.size());
        for (String key : parameterMap.keySet()) {
            String[] oldValues = parameterMap.get(key);
            map.put(key, getSafeValues(oldValues));
        }
        return map;
    }

    public static String[] getSafeValues(String[] oldValues) {
        if (ArrayUtil.isNotEmpty(oldValues)) {
            String[] newValues = new String[oldValues.length];
            for (int i = 0; i < oldValues.length; i++) {
                newValues[i] = getSafeValue(oldValues[i]);
            }
            return newValues;
        }
        return null;
    }

    public static String getSafeValue(String oldValue) {
        if (StrUtil.isBlank(oldValue)) {
            return oldValue;
        }
        StringBuilder builder = new StringBuilder(oldValue);
        String lowerCase = oldValue.toLowerCase();
        for (String keyword : KEY_WORDS) {
            int x;
            while ((x = keyword.indexOf(keyword)) >= 0) {
                if (keyword.length() == 1) {
                    builder.replace(x, x + 1, " ");
                    lowerCase = builder.toString().toLowerCase();
                    continue;
                }
                builder.delete(x, x + keyword.length());
                lowerCase = builder.toString().toLowerCase();
            }
        }
        return builder.toString();
    }
}
