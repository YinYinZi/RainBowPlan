package org.rainbow.cache.properties;

/**
 * 缓存类型枚举
 *
 * @author K
 * @date 2021/2/4  13:57
 */
public enum CacheType {

    /**
     * 本地缓存
     */
    CAFFINE,
    /**
     * Redis缓存
     */
    REDIS;

    private CacheType() {
    }

    public boolean eq(CacheType cacheType) {
        return cacheType != null && this.name().equals(cacheType.name());
    }
}
