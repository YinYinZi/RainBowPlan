package org.rainbow.core.context;

import cn.hutool.core.convert.Convert;
import org.rainbow.core.utils.StrPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 获取当前域中的 用户id appid 用户昵称
 *
 * @author K
 * @date 2021/1/25  11:50
 */
public class BaseContextHandler {
    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 账号id
     *
     * @return
     */
    public static Long getUserId() {
        return get(BaseContextConstants.JWT_KEY_USER_ID, Long.class, 0L);
    }

    public static String getUserIdStr() {
        return String.valueOf(getUserId());
    }

    /**
     * 账号id
     *
     * @param userId
     */
    public static void setUserId(Long userId) {
        set(BaseContextConstants.JWT_KEY_USER_ID, userId);
    }

    public static void setUserId(String userId) {
        set(BaseContextConstants.JWT_KEY_USER_ID, userId);
    }

    public static void set(String key, Object value) {
        Map<String, String> map = getLocalMap();
        map.put(key, Objects.isNull(value) ? StrPool.EMPTY : value.toString());
    }

    public static <T> T get(String key, Class<T> type) {
        Map<String, String> map = getLocalMap();
        return Convert.convert(type, map.get(key));
    }

    public static <T> T get(String key, Class<T> type, Object def) {
        Map<String, String> map = getLocalMap();
        return Convert.convert(type, map.getOrDefault(key, String.valueOf(null == def ? "" : def)));
    }

    public static String get(String key) {
        Map<String, String> map = getLocalMap();
        return map.getOrDefault(key, "");
    }

    public static Map<String, String> getLocalMap() {
        Map<String, String> map = THREAD_LOCAL.get();
        if (null == map) {
            map = new HashMap<>(10);
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, String> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
