package org.rainbow.cache.redis;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import org.rainbow.core.cache.CacheHashKey;
import org.rainbow.core.cache.CacheKey;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Redis操作类
 *
 * @author K
 * @date 2021/2/4  14:35
 */
public class RedisOps {
    private static final String KEY_NOT_NULL = "key不能为空";
    private static final String CACHE_KEY_NOT_NULL = "cacheKey不能为空";
    private static final Map<String, Object> KEY_LOCKS = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;
    private final HashOperations<String, Object, Object> hashOps;
    private final ListOperations<String, Object> listOps;
    private final SetOperations<String, Object> setOps;
    private final ZSetOperations<String, Object> zSetOps;
    private final boolean defaultCacheNullVal;

    public RedisOps(RedisTemplate<String, Object> redisTemplate, boolean defaultCacheNullVal) {
        this.redisTemplate = redisTemplate;
        Assert.notNull(redisTemplate, "redisTemplate 不能为空");
        this.valueOps = redisTemplate.opsForValue();
        this.hashOps = redisTemplate.opsForHash();
        this.listOps = redisTemplate.opsForList();
        this.setOps = redisTemplate.opsForSet();
        this.zSetOps = redisTemplate.opsForZSet();
        this.defaultCacheNullVal = defaultCacheNullVal;
    }

    private NullVal newNullVal() {
        return new NullVal();
    }

    private <T> boolean isNullVal(T value) {
        boolean isNull = value == null || NullVal.class.equals(value.getClass());
        return isNull || value.getClass().equals(Object.class) || value instanceof Map && ((Map)value).isEmpty();
    }

    public Long del(@NonNull CacheKey... keys) {
        return this.redisTemplate.delete(Arrays.asList(keys).stream().map(CacheKey::getKey).collect(Collectors.toList()));
    }

    public Long del(@NonNull String... keys) {
        return this.redisTemplate.delete(Arrays.asList(keys));
    }

    public Long del(@NonNull Collection<String> keys) {
        return this.redisTemplate.delete(keys);
    }

    public Set<String> keys(@NonNull String pattern) {
        return this.redisTemplate.keys(pattern);
    }

    public Boolean exists(@NonNull String key) {
        return this.redisTemplate.hasKey(key);
    }

    public String randomKey() {
        return this.redisTemplate.randomKey();
    }

    public void rename(@NonNull String oldKey, @NonNull String newKey) {
        this.redisTemplate.rename(oldKey, newKey);
    }

    public Boolean renameNx(@NonNull String oldKey, @NonNull String newKey) {
        return this.redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    public Boolean move(@NonNull String oldKey, int dbIndex) {
        return this.redisTemplate.move(oldKey, dbIndex);
    }

    public Boolean expire(@NonNull String key, long second) {
        return this.redisTemplate.expire(key, second, TimeUnit.SECONDS);
    }

    public Boolean expire(@NonNull String key, @NonNull Duration timeout) {
        return this.expire(key, timeout.getSeconds());
    }

    public Boolean expireAt(@NonNull String key, @NonNull Date date) {
        return this.redisTemplate.expireAt(key, date);
    }

    public Boolean expireAt(@NonNull String key, long unixTimeStamp) {
        return this.expireAt(key, new Date(unixTimeStamp));
    }

    public Boolean pExpire(@NonNull String key, long milliseconds) {
        return this.redisTemplate.expire(key, milliseconds, TimeUnit.MILLISECONDS);
    }

    public Boolean persist(@NonNull String key) {
        return this.redisTemplate.persist(key);
    }

    public String type(@NonNull String key) {
        DataType dataType = this.redisTemplate.type(key);
        return dataType == null ? DataType.NONE.code() : dataType.code();
    }

    public Long ttl(@NonNull String key) {
        return this.redisTemplate.getExpire(key);
    }

    public Long pTtl(@NonNull String key) {
        return this.redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }

    public void set(@NonNull String key, Object value, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : defaultCacheNullVal;
        if (cacheNullVal || value != null) {
            this.valueOps.set(key, value == null ? this.newNullVal() : value);
        }
    }

    public void set(@NonNull CacheKey cacheKey, Object value, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        String key = cacheKey.getKey();
        Duration expire = cacheKey.getExpire();
        if (expire == null) {
            this.set(key, value, cacheNullVal);
        } else {
            this.setEx(key, value, expire, cacheNullVal);
        }
    }

    public void setEx(String key, Object value, Duration timeout, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : defaultCacheNullVal;
        if (cacheNullVal || value != null) {
            this.valueOps.set(key, value == null ? this.newNullVal() : value, timeout);
        }
    }

    public void setEx(@NonNull String key, Object value, long seconds, boolean... cacheNullValues) {
        this.setEx(key, value, Duration.ofSeconds(seconds), cacheNullValues);
    }

    @Nullable
    public Boolean setXx(@NonNull String key, String value, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        return this.valueOps.setIfPresent(key, cacheNullVal && value == null ? this.newNullVal() : value);
    }

    @Nullable
    public Boolean setXx(@NonNull String key, String value, long seconds, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        return this.valueOps.setIfPresent(key, cacheNullVal && value == null ? this.newNullVal() : value, seconds, TimeUnit.SECONDS);
    }

    @Nullable
    public Boolean setXx(@NonNull String key, String value, Duration timeout, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        return this.valueOps.setIfPresent(key, cacheNullVal && value == null ? this.newNullVal() : value, timeout);
    }

    @Nullable
    public Boolean setNx(@NonNull String key, String value, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        return this.valueOps.setIfAbsent(key, cacheNullVal && value == null ? this.newNullVal() : value);
    }

    @Nullable
    public <T> T get(@NonNull String key, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        T value = (T) this.valueOps.get(key);
        if (value == null && cacheNullVal) {
            this.set(key, this.newNullVal(), true);
        }
        return this.isNullVal(value) ? null : value;
    }

    @Nullable
    public <T> T get(@NonNull String key, Function<String, T> loader, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        T value = this.get(key, false);
        if (value != null) {
            return this.isNullVal(value) ? null : value;
        } else {
            synchronized (KEY_LOCKS.computeIfAbsent(key, (v) -> new Object())) {
                value = this.get(key, false);
                if (value != null) {
                    return this.isNullVal(value) ? null : value;
                }

                try {
                    value = loader.apply(key);
                    this.set(key, value, cacheNullVal);
                } finally {
                    KEY_LOCKS.remove(key);
                }
            }
            return this.isNullVal(value) ? null : value;
        }
    }

    public <T> T getSet(@NonNull String key, Object value) {
        T val = (T) this.valueOps.getAndSet(key, value == null ? this.newNullVal() : value);
        return  this.isNullVal(val) ? null : val;
    }

    @Nullable
    public <T> T get(@NonNull CacheKey key, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        T value = (T) this.valueOps.get(key.getKey());
        if (value == null && cacheNullVal) {
            this.set((CacheKey)key, this.newNullVal(), true);
        }
        return this.isNullVal(value) ? null : value;
    }

    @Nullable
    public <T> T get(@NonNull CacheKey key, Function<CacheKey, T> loader, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        T value = this.get(key, false);
        if (value != null) {
            return this.isNullVal(value) ? null : value;
        } else {
            synchronized (KEY_LOCKS.computeIfAbsent(key.getKey(), (v) -> new Object())) {
                value = this.get(key, false);

                if (value != null) {
                    return this.isNullVal(value) ? null : value;
                }

                try {
                    value = loader.apply(key);
                    this.set(key, value, cacheNullVal);
                } finally {
                    KEY_LOCKS.remove(key.getKey());
                }
            }

            return this.isNullVal(value) ? null : value;
        }
    }

    @Nullable
    public Long strLen(@NonNull String key) {
        return this.valueOps.size(key);
    }

    @Nullable
    public Integer append(@NonNull String key, String value) {
        return this.valueOps.append(key, value);
    }

    public void setRange(@NonNull String key, String value, long offset) {
        this.valueOps.set(key, value, offset);
    }

    public String getRange(@NonNull String key, long start, long end) {
        return this.valueOps.get(key, start, end);
    }

    public void mSet(@NonNull Map<String, Object> map, boolean cacheNullVal) {
        Map<String, Object> mSetMap = this.mSetMap(map, cacheNullVal);
        this.valueOps.multiSet(mSetMap);
    }

    public void mSet(@NonNull Map<String, Object> map) {
        this.mSet(map, this.defaultCacheNullVal);
    }

    public void mSetNx(@NonNull Map<String, Object> map, boolean cacheNullVal) {
        Map<String, Object> mSetMap = this.mSetMap(map, cacheNullVal);
        this.valueOps.multiSetIfAbsent(mSetMap);
    }

    public void mSetNx(@NonNull Map<String, Object> map) {
        this.mSetNx(map, this.defaultCacheNullVal);
    }

    private Map<String, Object> mSetMap(@NonNull Map<String, Object> map, boolean cacheNullVal) {
        Map<String, Object> mSetMap = new HashMap(map.size());
        map.forEach((k, v) -> {
            if (v == null && cacheNullVal) {
                mSetMap.put(k, this.newNullVal());
            } else {
                mSetMap.put(k, v);
            }

        });
        return mSetMap;
    }

    public <T> List<T> mGet(@NonNull String... keys) {
        return this.mGet(Arrays.asList(keys));
    }

    public <T> List<T> mGet(@NonNull CacheKey... keys) {
        return this.mGetByCacheKey(Arrays.asList(keys));
    }

    public <T> List<T> mGet(@NonNull Collection<String> keys) {
        List<T> list = (List<T>) this.valueOps.multiGet(keys);
        return list == null ? null : (List)list.stream().map((item) -> {
            return this.isNullVal(item) ? null : item;
        }).collect(Collectors.toList());
    }

    public <T> List<T> mGetByCacheKey(@NonNull Collection<CacheKey> cacheKeys) {
        List<String> keys = (List)cacheKeys.stream().map(CacheKey::getKey).collect(Collectors.toList());
        List<T> list = (List<T>) this.valueOps.multiGet(keys);
        return list == null ? null : (List)list.stream().map((item) -> {
            return this.isNullVal(item) ? null : item;
        }).collect(Collectors.toList());
    }

    public Long incr(@NonNull String key) {
        return this.valueOps.increment(key);
    }

    public Long incrBy(@NonNull String key, long increment) {
        return this.valueOps.increment(key, increment);
    }

    public Double incrByFloat(@NonNull String key, double increment) {
        return this.valueOps.increment(key, increment);
    }

    public Long getCounter(@NonNull String key, Long... defaultValue) {
        Object val = this.valueOps.get(key);
        if (this.isNullVal(val)) {
            return defaultValue.length > 0 ? Convert.toLong(defaultValue[0]) : null;
        } else {
            return Convert.toLong(val);
        }
    }

    public Long decr(@NonNull String key) {
        return this.valueOps.decrement(key);
    }

    public Long decrBy(@NonNull String key, long decrement) {
        return this.valueOps.decrement(key, decrement);
    }

    public void hSet(@NonNull String key, @NonNull Object field, Object value, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        if (cacheNullVal || value != null) {
            this.hashOps.put(key, field, value == null ? this.newNullVal() : value);
        }
    }

    public void hSet(@NonNull CacheHashKey cacheHashKey, Object value, boolean... cacheNullValues) {
        this.hSet(cacheHashKey.getKey(), cacheHashKey.getField(), value, cacheNullValues);
        if (cacheHashKey.getExpire() != null) {
            this.expire(cacheHashKey.getKey(), cacheHashKey.getExpire());
        }
    }

    @Nullable
    public <T> T hGet(@NonNull String key, @NonNull Object field, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        T value = (T) this.hashOps.get(key, field);
        if (value == null && cacheNullVal) {
            this.hSet(key, field, this.newNullVal(), true);
        }
        return this.isNullVal(value) ? null : value;
    }

    @Nullable
    public <T> T hGet(@NonNull String key, @NonNull Object field, BiFunction<String, Object, T> loader, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        T value = this.hGet(key, field, false);
        if (value != null) {
            return this.isNullVal(value) ? null : value;
        } else {
            String lockKey = key + "@" + field;
            synchronized(KEY_LOCKS.computeIfAbsent(lockKey, (v) -> new Object())) {
                value = this.hGet(key, field, false);
                if (value != null) {
                    return this.isNullVal(value) ? null : value;
                }

                try {
                    value = loader.apply(key, field);
                    this.hSet(key, field, value, cacheNullVal);
                } finally {
                    KEY_LOCKS.remove(lockKey);
                }
            }

            return this.isNullVal(value) ? null : value;
        }
    }

    @Nullable
    public <T> T hGet(@NonNull CacheHashKey key, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        T value = (T) this.hashOps.get(key.getKey(), key.getField());
        if (value == null && cacheNullVal) {
            this.hSet(key, this.newNullVal(), true);
        }
        return this.isNullVal(value) ? null : value;
    }

    @Nullable
    public <T> T hGet(@NonNull CacheHashKey key, Function<CacheHashKey, T> loader, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        T value = this.hGet(key, false);
        if (value != null) {
            return this.isNullVal(value) ? null : value;
        } else {
            String lockKey = key.getKey() + "@" + key.getField();
            synchronized(KEY_LOCKS.computeIfAbsent(lockKey, (v) -> {
                return new Object();
            })) {
                value = this.hGet(key, false);
                if (value != null) {
                    return this.isNullVal(value) ? null : value;
                }

                try {
                    value = loader.apply(key);
                    this.hSet(key, value, cacheNullVal);
                } finally {
                    KEY_LOCKS.remove(key.getKey());
                }
            }

            return this.isNullVal(value) ? null : value;
        }
    }

    public Boolean hExists(@NonNull String key, @NonNull Object field) {
        return this.hashOps.hasKey(key, field);
    }

    public Boolean hExists(@NonNull CacheHashKey cacheHashKey) {
        return this.hashOps.hasKey(cacheHashKey.getKey(), cacheHashKey.getField());
    }

    public Long hDel(@NonNull String key, Object... fields) {
        return this.hashOps.delete(key, fields);
    }

    public Long hLen(@NonNull String key) {
        return this.hashOps.size(key);
    }

    public Long hStrLen(@NonNull String key, @NonNull Object field) {
        return this.hashOps.lengthOfValue(key, field);
    }

    public Long hIncrBy(@NonNull String key, @NonNull Object field, long increment) {
        return this.hashOps.increment(key, field, increment);
    }

    public Double hIncrByFloat(@NonNull String key, @NonNull Object field, double increment) {
        return this.hashOps.increment(key, field, increment);
    }

    public void hmSet(@NonNull String key, @NonNull Map<Object, Object> hash, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : this.defaultCacheNullVal;
        Map<Object, Object> newMap = new HashMap(hash.size());
        hash.forEach((k, v) -> {
            if (v == null && cacheNullVal) {
                newMap.put(k, this.newNullVal());
            } else {
                newMap.put(k, v);
            }

        });
        this.hashOps.putAll(key, newMap);
    }

    public List<Object> hmGet(@NonNull String key, @NonNull Object... fields) {
        return this.hmGet(key, Arrays.asList(fields));
    }

    public List<Object> hmGet(@NonNull String key, @NonNull Collection<Object> fields) {
        List<Object> list = this.hashOps.multiGet(key, fields);
        return list.stream().map((item) -> this.isNullVal(item) ? null : item).collect(Collectors.toList());
    }

    public Set<Object> hKeys(@NonNull String key) {
        return this.hashOps.keys(key);
    }

    public List<Object> hVals(@NonNull String key) {
        return this.hashOps.values(key);
    }

    public Map<Object, Object> hGetAll(@NonNull String key) {
        return this.hashOps.entries(key);
    }

    @Nullable
    public Long lPush(@NonNull String key, Object... values) {
        return this.listOps.leftPushAll(key, values);
    }

    @Nullable
    public Long lPush(@NonNull String key, Collection<Object> values) {
        return this.listOps.leftPushAll(key, values);
    }

    @Nullable
    public Long lPushX(@NonNull String key, Object values) {
        return this.listOps.leftPushIfPresent(key, values);
    }

    @Nullable
    public Long rPush(@NonNull String key, Object... values) {
        return this.listOps.rightPushAll(key, values);
    }

    @Nullable
    public Long rPush(@NonNull String key, Collection<Object> values) {
        return this.listOps.rightPushAll(key, values);
    }

    @Nullable
    public Long rPushX(@NonNull String key, Object value) {
        return this.listOps.rightPushIfPresent(key, value);
    }

    @Nullable
    public <T> T lPop(@NonNull String key) {
        return (T) this.listOps.leftPop(key);
    }

    public <T> T rPop(@NonNull String key) {
        return (T) this.listOps.rightPop(key);
    }

    public <T> T rPoplPush(String sourceKey, String destinationKey) {
        return (T) this.listOps.rightPopAndLeftPush(sourceKey, destinationKey);
    }

    @Nullable
    public Long lRem(@NonNull String key, long count, Object value) {
        return this.listOps.remove(key, count, value);
    }

    @Nullable
    public Long lLen(@NonNull String key) {
        return this.listOps.size(key);
    }

    @Nullable
    public <T> T lIndex(@NonNull String key, long index) {
        return (T) this.listOps.index(key, index);
    }

    @Nullable
    public Long lInsert(@NonNull String key, Object pivot, Object value) {
        return this.listOps.leftPush(key, pivot, value);
    }

    @Nullable
    public Long rInsert(@NonNull String key, Object pivot, Object value) {
        return this.listOps.rightPush(key, pivot, value);
    }

    public void lSet(@NonNull String key, long index, Object value) {
        this.listOps.set(key, index, value);
    }

    @Nullable
    public List<Object> lRange(@NonNull String key, long start, long end) {
        return this.listOps.range(key, start, end);
    }

    public void lTrim(@NonNull String key, long start, long end) {
        this.listOps.trim(key, start, end);
    }

    public Long sAdd(@NonNull String key, Object... members) {
        return this.setOps.add(key, members);
    }

    public Long sAdd(@NonNull String key, Collection<Object> members) {
        return this.setOps.add(key, members.toArray());
    }

    public Boolean sIsMember(@NonNull String key, Object member) {
        return this.setOps.isMember(key, member);
    }

    @Nullable
    public <T> T sPop(@NonNull String key) {
        return (T) this.setOps.pop(key);
    }

    @Nullable
    public <T> T sRandMember(@NonNull String key) {
        return (T) this.setOps.randomMember(key);
    }

    @Nullable
    public Set<Object> sRandMember(@NonNull String key, long count) {
        return this.setOps.distinctRandomMembers(key, count);
    }

    @Nullable
    public List<Object> sRandMembers(@NonNull String key, long count) {
        return this.setOps.randomMembers(key, count);
    }

    @Nullable
    public Long sRem(@NonNull String key, Object... members) {
        return this.setOps.remove(key, members);
    }

    public Boolean sMove(@NonNull String sourceKey, String destinationKey, Object value) {
        return this.setOps.move(sourceKey, value, destinationKey);
    }

    public Long sCard(@NonNull String key) {
        return this.setOps.size(key);
    }

    @Nullable
    public Set<Object> sMembers(@NonNull String key) {
        return this.setOps.members(key);
    }

    @Nullable
    public Set<Object> sInter(@NonNull String key, @NonNull String otherKey) {
        return this.setOps.intersect(key, otherKey);
    }

    @Nullable
    public Set<Object> sInter(@NonNull String key, Collection<String> otherKeys) {
        return this.setOps.intersect(key, otherKeys);
    }

    @Nullable
    public Set<Object> sInter(Collection<String> otherKeys) {
        return this.setOps.intersect(otherKeys);
    }

    @Nullable
    public Long sInterStore(@NonNull String key, @NonNull String otherKey, @NonNull String destKey) {
        return this.setOps.intersectAndStore(key, otherKey, destKey);
    }

    @Nullable
    public Long sInterStore(@NonNull String key, Collection<String> otherKeys, @NonNull String destKey) {
        return this.setOps.intersectAndStore(key, otherKeys, destKey);
    }

    @Nullable
    public Long sInterStore(Collection<String> otherKeys, @NonNull String destKey) {
        return this.setOps.intersectAndStore(otherKeys, destKey);
    }

    @Nullable
    public Set<Object> sUnion(@NonNull String key, @NonNull String otherKey) {
        return this.setOps.union(key, otherKey);
    }

    @Nullable
    public Set<Object> sUnion(@NonNull String key, Collection<String> otherKeys) {
        return this.setOps.union(key, otherKeys);
    }

    @Nullable
    public Set<Object> sUnion(Collection<String> otherKeys) {
        return this.setOps.union(otherKeys);
    }

    public Long sUnionStore(@NonNull String key, @NonNull String otherKey, @NonNull String distKey) {
        return this.setOps.unionAndStore(key, otherKey, distKey);
    }

    public Long sUnionStore(Collection<String> otherKeys, @NonNull String distKey) {
        return this.setOps.unionAndStore(otherKeys, distKey);
    }

    @Nullable
    public Set<Object> sDiff(@NonNull String key, @NonNull String otherKey) {
        return this.setOps.difference(key, otherKey);
    }

    public Set<Object> sDiff(Collection<String> otherKeys) {
        return this.setOps.difference(otherKeys);
    }

    public Long sDiffStore(@NonNull String key, @NonNull String otherKey, @NonNull String distKey) {
        return this.setOps.differenceAndStore(key, otherKey, distKey);
    }

    public Long sDiffStore(Collection<String> otherKeys, @NonNull String distKey) {
        return this.setOps.differenceAndStore(otherKeys, distKey);
    }

    public Boolean zAdd(@NonNull String key, Object member, double score) {
        return this.zSetOps.add(key, member, score);
    }

    public Long zAdd(@NonNull String key, Map<Object, Double> scoreMembers) {
        Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet();
        scoreMembers.forEach((score, member) -> {
            tuples.add(new DefaultTypedTuple(score, member));
        });
        return this.zSetOps.add(key, tuples);
    }

    public Double zScore(@NonNull String key, Object member) {
        return this.zSetOps.score(key, member);
    }

    public Double zIncrBy(@NonNull String key, Object member, double score) {
        return this.zSetOps.incrementScore(key, member, score);
    }

    public Long zCard(@NonNull String key) {
        return this.zSetOps.zCard(key);
    }

    public Long zCount(@NonNull String key, double min, double max) {
        return this.zSetOps.count(key, min, max);
    }

    @Nullable
    public Set<Object> zRange(@NonNull String key, long start, long end) {
        return this.zSetOps.range(key, start, end);
    }

    @Nullable
    public Set<ZSetOperations.TypedTuple<Object>> zRangeWithScores(@NonNull String key, long start, long end) {
        return this.zSetOps.rangeWithScores(key, start, end);
    }

    @Nullable
    public Set<Object> zRevrange(@NonNull String key, long start, long end) {
        return this.zSetOps.reverseRange(key, start, end);
    }

    @Nullable
    public Set<ZSetOperations.TypedTuple<Object>> zRevrangeWithScores(@NonNull String key, long start, long end) {
        return this.zSetOps.reverseRangeWithScores(key, start, end);
    }

    public Set<Object> zRangeByScore(@NonNull String key, double min, double max) {
        return this.zSetOps.rangeByScore(key, min, max);
    }

    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(@NonNull String key, double min, double max) {
        return this.zSetOps.rangeByScoreWithScores(key, min, max);
    }

    public Set<Object> zReverseRange(@NonNull String key, double min, double max) {
        return this.zSetOps.reverseRangeByScore(key, min, max);
    }

    public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(@NonNull String key, double min, double max) {
        return this.zSetOps.reverseRangeByScoreWithScores(key, min, max);
    }

    @Nullable
    public Long zRank(@NonNull String key, Object member) {
        return this.zSetOps.rank(key, member);
    }

    public Long zRevrank(@NonNull String key, Object member) {
        return this.zSetOps.reverseRank(key, member);
    }

    public Long zRem(@NonNull String key, Object... members) {
        return this.zSetOps.remove(key, members);
    }

    public Long zRem(@NonNull String key, long start, long end) {
        return this.zSetOps.removeRange(key, start, end);
    }

    public Long zRemRangeByScore(@NonNull String key, double min, double max) {
        return this.zSetOps.removeRangeByScore(key, min, max);
    }

    public ValueOperations<String, Object> getValueOps() {
        return valueOps;
    }

    public HashOperations<String, Object, Object> getHashOps() {
        return hashOps;
    }

    public ListOperations<String, Object> getListOps() {
        return listOps;
    }

    public SetOperations<String, Object> getSetOps() {
        return setOps;
    }

    public ZSetOperations<String, Object> getzSetOps() {
        return zSetOps;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public boolean isDefaultCacheNullVal() {
        return defaultCacheNullVal;
    }
}
