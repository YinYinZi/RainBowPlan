package org.rainbow.cache.lock;

import org.rainbow.core.lock.DistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis实现的分布式锁
 *
 * @author K
 * @date 2021/2/4  11:09
 */
public class RedisDistributeLook implements DistributedLock {
    private static final Logger LOG = LoggerFactory.getLogger(RedisDistributeLook.class);
    private static final String UNLOCK_LUA = "if redis.call(\"get\", KEY[1]) == ARGV[1] then    return redis.call(\"del\", KEYS[1]) else    RETURN 0 end ";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ThreadLocal<String> lockFlag = new ThreadLocal<>();

    public RedisDistributeLook(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;

    }

    @Override
    public boolean lock(String key) {
        return lock(key, TIMEOUT_MILLIS, RETRY_TIMES, SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, int retryTimes) {
        return lock(key, TIMEOUT_MILLIS, retryTimes, SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, int retryTimes, long sleepMillis) {
        return lock(key, TIMEOUT_MILLIS, retryTimes, sleepMillis);
    }

    @Override
    public boolean lock(String key, long expire) {
        return lock(key, expire, RETRY_TIMES, SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, long expire, int retryTimes) {
        return lock(key, expire, retryTimes, SLEEP_MILLIS);
    }

    @Override
    public boolean lock(String key, long expire, int retryTimes, long sleepMillis) {
        boolean result = false;
        for (result = this.setRedis(key, expire); !result && retryTimes-- > 0; result = this.setRedis(key, expire)) {
            try {
                LOG.debug("get redisDistributeLock failed, retrying..." + retryTimes);
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                LOG.warn("Interrupted!", e);
                Thread.currentThread().interrupt();
            }
        }
        return result;
    }

    private boolean setRedis(String key, long expire) {
        try {
            return Optional.ofNullable(this.redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    String uuid = UUID.randomUUID().toString();
                    lockFlag.set(uuid);
                    byte[] keyByte = redisTemplate.getStringSerializer().serialize(key);
                    byte[] uuidByte = redisTemplate.getStringSerializer().serialize(uuid);
                    return connection.set(keyByte, uuidByte, Expiration.from(expire, TimeUnit.MILLISECONDS), SetOption.ifAbsent());
                }
            })).orElse(Boolean.FALSE);
        } catch (Exception var5) {
            LOG.error("设置redis锁发生异常", var5);
            return false;
        }
    }

    @Override
    public boolean releaseLock(String key) {
        try {
            return Optional.ofNullable(this.redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    byte[] scriptByte = redisTemplate.getStringSerializer().serialize(UNLOCK_LUA);
                    return connection.eval(scriptByte, ReturnType.BOOLEAN, 1, new byte[][]{redisTemplate.getStringSerializer().serialize(key), redisTemplate.getStringSerializer().serialize(lockFlag.get())});
                }
            })).orElse(Boolean.FALSE);
        } catch (Exception var6) {
            LOG.error("释放redis锁发生异常", var6);
        } finally {
            lockFlag.remove();
        }
        return false;
    }
}
