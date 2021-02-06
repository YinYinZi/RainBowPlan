package org.rainbow.core.cache;

import org.springframework.lang.NonNull;

import java.time.Duration;

/**
 * 缓存key
 *
 * @author K
 * @date 2021/2/4  15:05
 */
public class CacheKey {
    @NonNull
    private String key;
    private Duration expire;

    public CacheKey(@NonNull String key) {
        this.key = key;
    }

    public CacheKey(@NonNull String key, Duration expire) {
        this.key = key;
        this.expire = expire;
    }

    public CacheKey() {
    }

    @NonNull
    public String getKey() {
        return key;
    }

    public Duration getExpire() {
        return expire;
    }

    public void setKey(@NonNull String key) {
       this.key = key;
    }

    public void setExpire(Duration expire) {
        this.expire = expire;
    }

    protected boolean canEqual(Object other) {
        return other instanceof CacheKey;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CacheKey)) {
            return false;
        } else {
            CacheKey other = (CacheKey)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object thisKey = this.getKey();
                Object otherKey = other.getKey();
                if (thisKey == null) {
                    if (otherKey != null) {
                        return false;
                    }
                } else if (!thisKey.equals(otherKey)) {
                    return false;
                }

                Object thisExpire = this.getExpire();
                Object otherExpire = other.getExpire();
                if (thisExpire == null) {
                    if (otherExpire != null) {
                        return false;
                    }
                } else if (!thisExpire.equals(otherExpire)) {
                    return false;
                }

                return true;
            }
        }
    }

    @Override
    public int hashCode() {
        int result = 1;
        Object key = this.getKey();
        result = result * 59 + key.hashCode();
        Object expire = this.getExpire();
        result = result * 59 + (expire == null ? 43 : expire.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "CacheKey(key=" + this.getKey() + ", expire=" + this.getExpire() + ")";
    }
}
