package org.slf4j;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.spi.MDCAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author K
 * @date 2021/2/14  11:18
 */
public class RainbowMdcAdapter implements MDCAdapter {
    private static final int WRITE_OPERATION = 1;
    private static final int MAP_COPY_OPERATION = 2;
    private static RainbowMdcAdapter mdcAdapter = new RainbowMdcAdapter();
    private final ThreadLocal<Map<String, String>> copyOnInheritThreadLocal = new TransmittableThreadLocal<>();
    private final ThreadLocal<Integer> lastOperation = new ThreadLocal<>();

    public RainbowMdcAdapter() {
    }

    public static RainbowMdcAdapter getInstance() {
        return mdcAdapter;
    }

    private static boolean wasLastOpReadOrNull(Integer lastOp) {
        return lastOp == null || lastOp == 2;
    }

    private Integer getAndSetLastOperation(int op) {
        Integer lastOp = this.lastOperation.get();
        this.lastOperation.set(op);
        return lastOp;
    }

    private Map<String, String> duplicateAndInsertNewMap(Map<String, String> oldMap) {
        Map<String, String> newMap = Collections.synchronizedMap(new HashMap(16));
        if (oldMap != null) {
            synchronized (oldMap) {
                newMap.putAll(oldMap);
            }
        }

        this.copyOnInheritThreadLocal.set(newMap);
        return newMap;
    }

    @Override
    public void put(String key, String val) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        } else {
            Map<String, String> oldMap = this.copyOnInheritThreadLocal.get();
            Integer lastOp = this.getAndSetLastOperation(1);
            if (!wasLastOpReadOrNull(lastOp) && oldMap != null) {
                oldMap.put(key, val);
            } else {
                Map<String, String> newMap = this.duplicateAndInsertNewMap(oldMap);
                newMap.put(key, val);
            }
        }
    }

    @Override
    public String get(String key) {
        Map<String, String> map = this.copyOnInheritThreadLocal.get();
        return map != null && key != null ? map.get(key) : null;
    }

    @Override
    public void remove(String key) {
        if (key != null) {
            Map<String, String> oldMap = this.copyOnInheritThreadLocal.get();
            if (oldMap != null) {
                Integer lastOp = this.getAndSetLastOperation(1);
                if (wasLastOpReadOrNull(lastOp)) {
                    Map<String, String> newMap = this.duplicateAndInsertNewMap(oldMap);
                    newMap.remove(key);
                } else {
                    oldMap.remove(key);
                }
            }
        }
    }

    @Override
    public void clear() {
        this.lastOperation.set(1);
        this.copyOnInheritThreadLocal.remove();
    }

    public Map<String, String> getPropertyMap() {
        this.lastOperation.set(2);
        return this.copyOnInheritThreadLocal.get();
    }

    public Set<String> getKeys() {
        Map<String, String> map = this.getPropertyMap();
        return map != null ? map.keySet() : null;
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        Map<String, String> hashMap = this.copyOnInheritThreadLocal.get();
        return hashMap == null ? null : new HashMap<>(hashMap);
    }

    @Override
    public void setContextMap(Map<String, String> contextMap) {
        this.lastOperation.set(1);
        Map<String, String> newMap = Collections.synchronizedMap(new HashMap(16));
        newMap.putAll(contextMap);
        this.copyOnInheritThreadLocal.set(newMap);
    }

    static {
        MDC.mdcAdapter = mdcAdapter;
    }
}
