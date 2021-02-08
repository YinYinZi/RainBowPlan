package org.rainbow.cache.repository.impl;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.rainbow.cache.repository.CacheOps;
import org.rainbow.core.cache.CacheHashKey;
import org.rainbow.core.cache.CacheKey;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 本次缓存实现的操作类
 *
 * @author K
 * @date 2021/2/5  15:40
 */
public class CaffeineOpsImpl implements CacheOps {
    private final static long DEF_MAX_SIZE = 1000L;
    private final Cache<String, Cache<String, Object>> cacheMap = Caffeine.newBuilder().maximumSize(1000L).build();

    public CaffeineOpsImpl() {

    }

    @Override
    public Long del(CacheKey... keys) {
        CacheKey[] cacheKeys = keys;
        int length = keys.length;

        for (int i = 0; i < length; i++) {
            CacheKey key = cacheKeys[i];
            this.cacheMap.invalidate(key.getKey());
        }
        return (long)keys.length;
    }

    @Override
    public Long del(String... keys) {
        String[] keyArr = keys;
        int length = keys.length;

        for (int i = 0; i < length; i ++) {
            String key = keyArr[i];
            this.cacheMap.invalidate(key);
        }
        return (long)keys.length;
    }

    @Override
    public Boolean exists(CacheKey key) {
        Cache<String, Object> cache = this.cacheMap.getIfPresent(key.getKey());
        if (cache == null) {
            return false;
        } else {
            cache.cleanUp();
            return cache.estimatedSize() > 0L;
        }
    }

    @Override
    public void set(CacheKey key, Object value, boolean... cacheNullValues) {
        if (value != null) {
            Caffeine<Object, Object> builder = Caffeine.newBuilder().maximumSize(1000L);
            if (key.getExpire() != null) {
                builder.expireAfterWrite(key.getExpire());
            }

            Cache<String, Object> cache = builder.build();
            cache.put(key.getKey(), value);
            this.cacheMap.put(key.getKey(), cache);
        }
    }

    @Override
    public <T> T get(CacheKey key, boolean... cacheNullValues) {
        Cache<String, Object> ifPresent = this.cacheMap.getIfPresent(key.getKey());
        return ifPresent == null ? null : (T) ifPresent.getIfPresent(key);
    }

    @Override
    public <T> T get(String key, boolean... cacheNullValues) {
        Cache<String, Object> ifPresent = this.cacheMap.getIfPresent(key);
        return ifPresent == null ? null : (T) ifPresent.getIfPresent(key);
    }

    @Override
    public <T> List<T> find(Collection<CacheKey> keys) {
        return (List) keys.stream().map((k) -> this.get(k, false)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public <T> T get(CacheKey key, Function<CacheKey, ? extends T> loader, boolean... cacheNullValues) {
        Cache<String, Object> cache = this.cacheMap.get(key.getKey(), (k) -> {
            Caffeine<Object, Object> builder = Caffeine.newBuilder().maximumSize(1000L);
            if (key.getExpire() != null) {
                builder.expireAfterWrite(key.getExpire());
            }

            Cache<String, Object> newCache = builder.build();
            newCache.get(k, (tk) -> loader.apply(new CacheKey(tk)));
            return newCache;
        });
        return (T) cache.getIfPresent(key.getKey());
    }

    @Override
    public void flushDb() {
        this.cacheMap.invalidateAll();
    }

    @Override
    public Long incr(CacheKey key) {
        Long old = this.get(key, (k) -> 0L);
        Long newVal = old + 1L;
        this.set(key, newVal);
        return newVal;
    }

    @Override
    public Long incrBy(CacheKey key, long increment) {
        Long old = this.get(key, (k) -> 0L);
        Long newVal = old + increment;
        this.set(key, newVal);
        return newVal;
    }

    @Override
    public Double incrByFloat(CacheKey key, double increment) {
        Double old = this.get(key, (k) -> 0.0D);
        Double newVal = old + increment;
        this.set(key, newVal);
        return newVal;
    }

    @Override
    public Long decr(CacheKey key) {
        Long old = this.get(key, (k) -> 0L);
        Long newVal = old - 1;
        this.set(key, newVal);
        return newVal;
    }

    @Override
    public Long decrBy(CacheKey key, long decrement) {
        Long old = this.get(key, (k) -> 0L);
        Long newVal = old - decrement;
        this.set(key, newVal);
        return newVal;
    }

    @Override
    public Set<String> keys(String pattern) {
        if (StrUtil.isEmpty(pattern)) {
            return Collections.EMPTY_SET;
        } else {
            ConcurrentMap<String, Cache<String, Object>> map = this.cacheMap.asMap();
            Set<String> list = new HashSet();
            map.forEach((k, val) -> {
                if ("*".equals(pattern)) {
                    list.add(k);
                } else if (!pattern.contains("?")) {
                    if (!pattern.contains("*")) {
                        if (!pattern.contains("[") || !pattern.contains("]")) {

                        }
                    }
                }
            });
        }
        return null;
    }

    @Override
    public Boolean expire(CacheKey cacheKey) {
        return true;
    }

    @Override
    public Boolean persist(CacheKey cacheKey) {
        return true;
    }

    @Override
    public String type(CacheKey cacheKey) {
        return "caffeine";
    }

    @Override
    public Long ttl(CacheKey cacheKey) {
        return -1L;
    }

    @Override
    public Long pTtl(CacheKey cacheKey) {
        return -1L;
    }

    @Override
    public void hSet(CacheHashKey key, Object value, boolean... cacheNullValues) {
        this.set(key.tran(), value, cacheNullValues);
    }

    @Override
    public <T> T hGet(CacheHashKey key, boolean... cacheNullValues) {
        return this.get(key.tran(), cacheNullValues);
    }

    @Override
    public <T> T hGet(CacheHashKey key, Function<CacheHashKey, T> loader, boolean... cacheNullValue) {
        Function<CacheKey, T> ckLoader = (k) -> loader.apply(key);
        return this.get(key.tran(), ckLoader, cacheNullValue);
    }

    @Override
    public Boolean hExists(CacheHashKey cacheHashKey) {
        return this.exists(cacheHashKey.tran());
    }

    @Override
    public Long hDel(String key, Object... fields) {
        Object[] fieldArr = fields;
        int length = fields.length;

        for (int i = 0; i < length; i++) {
            Object field = fieldArr[i];
            this.cacheMap.invalidate(StrUtil.join(":", new Object[]{key, field}));
        }
        return (long)fields.length;
    }

    @Override
    public Long hDel(CacheHashKey cacheHashKey) {
        this.cacheMap.invalidate(cacheHashKey.tran().getKey());
        return 1L;
    }

    @Override
    public Long hLen(CacheHashKey cacheHashKey) {
        return 0L;
    }

    @Override
    public Long hIncrBy(CacheHashKey key, long increment) {
        return this.incrBy(key.tran(), increment);
    }

    @Override
    public Double hIncrBy(CacheHashKey key, double increment) {
        return this.incrByFloat(key.tran(), increment);
    }

    @Override
    public Set<Object> hKeys(CacheHashKey cacheHashKey) {
        return Collections.emptySet();
    }

    @Override
    public List<Object> hVals(CacheHashKey cacheHashKey) {
        return Collections.emptyList();
    }

    @Override
    public Map<Object, Object> hGetAll(CacheHashKey cacheHashKey) {
        return Collections.emptyMap();
    }

    @Override
    public Long sAdd(CacheKey cacheKey, Object val) {
        return 0L;
    }

    @Override
    public Long sRem(CacheKey cacheKey, Object... values) {
        return 0L;
    }

    @Override
    public Set<Object> sMembers(CacheKey cacheKey) {
        return Collections.emptySet();
    }

    @Override
    public <T> T sPop(CacheKey cacheKey) {
        return null;
    }

    @Override
    public Long sCard(CacheKey cacheKey) {
        return 0L;
    }
}
