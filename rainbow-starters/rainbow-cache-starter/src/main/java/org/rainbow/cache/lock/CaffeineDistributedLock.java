package org.rainbow.cache.lock;

import org.rainbow.core.lock.DistributedLock;

/**
 * 本地缓存实现分布式锁
 *
 * @author K
 * @date 2021/2/4  11:06
 */
public class CaffeineDistributedLock implements DistributedLock {

    public CaffeineDistributedLock() {
    }

    @Override
    public boolean lock(String key) {
        return true;
    }

    @Override
    public boolean lock(String key, int retryTimes) {
        return true;
    }

    @Override
    public boolean lock(String key, int retryTimes, long sleepMillis) {
        return true;
    }

    @Override
    public boolean lock(String key, long expire) {
        return true;
    }

    @Override
    public boolean lock(String key, long expire, int retryTimes) {
        return true;
    }

    @Override
    public boolean lock(String key, long expire, int retryTimes, long sleepMillis) {
        return true;
    }

    @Override
    public boolean releaseLock(String key) {
        return true;
    }
}
