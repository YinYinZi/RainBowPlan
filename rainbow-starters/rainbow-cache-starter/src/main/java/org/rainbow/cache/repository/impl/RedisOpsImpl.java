package org.rainbow.cache.repository.impl;

import org.rainbow.cache.redis.RedisOps;
import org.rainbow.cache.repository.CacheOps;
import org.rainbow.core.cache.CacheHashKey;
import org.rainbow.core.cache.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author K
 * @date 2021/2/5  17:12
 */
public class RedisOpsImpl implements CacheOps {
    private static final Logger log = LoggerFactory.getLogger(RedisOpsImpl.class);
    private final RedisOps redisOps;

    public RedisOpsImpl(RedisOps redisOps) {
        this.redisOps = redisOps;
    }

    @Override
    public Long del(@NonNull CacheKey... keys) {
        return this.redisOps.del(keys);
    }

    @Override
    public Long del(String... keys) {
        return this.redisOps.del(keys);
    }

    @Override
    public Boolean exists(@NonNull CacheKey key) {
        return this.redisOps.exists(key.getKey());
    }

    @Override
    public void set(@NonNull CacheKey key, Object value, boolean... cacheNullValues) {
        this.redisOps.set(key, value, cacheNullValues);
    }

    @Override
    public <T> T get(@NonNull CacheKey key, boolean... cacheNullValues) {
        return this.redisOps.get(key, cacheNullValues);
    }

    @Override
    public <T> T get(String key, boolean... cacheNullValues) {
        return this.redisOps.get(key, cacheNullValues);
    }

    @Override
    public <T> List<T> find(@NonNull Collection<CacheKey> keys) {
        return this.redisOps.mGetByCacheKey(keys);
    }

    @Override
    public <T> T get(@NonNull CacheKey key, Function<CacheKey, ? extends T> loader, boolean... cacheNullValues) {
        return this.redisOps.get(key, loader, cacheNullValues);
    }

    @Override
    public void flushDb() {
        this.redisOps.getRedisTemplate().execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "ok";
            }
        });
    }

    @Override
    public Long incr(@NonNull CacheKey key) {
        return this.redisOps.incr(key.getKey());
    }

    @Override
    public Long incrBy(@NonNull CacheKey key, long increment) {
        return this.redisOps.incrBy(key.getKey(), increment);
    }

    @Override
    public Double incrByFloat(@NonNull CacheKey key, double increment) {
        return this.redisOps.incrByFloat(key.getKey(), increment);
    }

    @Override
    public Long decr(@NonNull CacheKey key) {
        return this.redisOps.decr(key.getKey());
    }

    @Override
    public Long decrBy(@NonNull CacheKey key, long decrement) {
        return this.redisOps.decrBy(key.getKey(), decrement);
    }

    @Override
    public Set<String> keys(@NonNull String pattern) {
        return this.redisOps.keys(pattern);
    }

    @Override
    public Boolean expire(@NonNull CacheKey key) {
        assert key.getExpire() != null;
        return this.redisOps.expire(key.getKey(), key.getExpire());
    }

    @Override
    public Boolean persist(@NonNull CacheKey key) {
        return this.redisOps.persist(key.getKey());
    }

    @Override
    public String type(@NonNull CacheKey key) {
        return this.redisOps.type(key.getKey());
    }

    @Override
    public Long ttl(@NonNull CacheKey key) {
        return this.redisOps.ttl(key.getKey());
    }

    @Override
    public Long pTtl(@NonNull CacheKey key) {
        return this.redisOps.pTtl(key.getKey());
    }

    @Override
    public void hSet(@NonNull CacheHashKey key, Object value, boolean... cacheNullValues) {
        this.redisOps.hSet(key, value, cacheNullValues);
    }

    @Override
    public <T> T hGet(@NonNull CacheHashKey key, boolean... cacheNullValues) {
        return this.redisOps.hGet(key, cacheNullValues);
    }

    @Override
    public <T> T hGet(CacheHashKey cacheHashKey, Function<CacheHashKey, T> function, boolean... cacheNullValue) {
        return this.redisOps.hGet(cacheHashKey, function, cacheNullValue);
    }

    @Override
    public Boolean hExists(@NonNull CacheHashKey cacheHashKey) {
        return this.redisOps.hExists(cacheHashKey);
    }

    @Override
    public Long hDel(@NonNull String key, Object... fields) {
        return this.redisOps.hDel(key, fields);
    }

    @Override
    public Long hDel(@NonNull CacheHashKey cacheHashKey) {
        return this.redisOps.hDel(cacheHashKey.getKey(), new Object[]{cacheHashKey.getField()});
    }

    @Override
    public Long hLen(@NonNull CacheHashKey key) {
        return this.redisOps.hLen(key.getKey());
    }

    @Override
    public Long hIncrBy(@NonNull CacheHashKey key, long increment) {
        return this.redisOps.hIncrBy(key.getKey(), key.getField(), increment);
    }

    @Override
    public Double hIncrBy(@NonNull CacheHashKey key, double increment) {
        return this.redisOps.hIncrByFloat(key.getKey(), key.getField(), increment);
    }

    @Override
    public Set<Object> hKeys(@NonNull CacheHashKey key) {
        return this.redisOps.hKeys(key.getKey());
    }

    @Override
    public List<Object> hVals(@NonNull CacheHashKey key) {
        return this.redisOps.hVals(key.getKey());
    }

    @Override
    public Map<Object, Object> hGetAll(@NonNull CacheHashKey key) {
        return this.redisOps.hGetAll(key.getKey());
    }

    @Override
    public Long sAdd(@NonNull CacheKey key, Object value) {
        Long result = this.redisOps.sAdd(key.getKey(), new Object[]{value});
        if (key.getExpire() != null) {
            this.redisOps.expire(key.getKey(), key.getExpire());
        }

        return result;
    }

    @Override
    public Long sRem(@NonNull CacheKey key, Object... members) {
        return this.redisOps.sRem(key.getKey(), members);
    }

    @Override
    public Set<Object> sMembers(@NonNull CacheKey key) {
        return this.redisOps.sMembers(key.getKey());
    }

    @Override
    public <T> T sPop(@NonNull CacheKey key) {
        return this.redisOps.sPop(key.getKey());
    }

    @Override
    public Long sCard(@NonNull CacheKey key) {
        return this.redisOps.sCard(key.getKey());
    }
}
