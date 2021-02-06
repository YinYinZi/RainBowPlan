package org.rainbow.core.cache;

import cn.hutool.core.util.StrUtil;
import org.springframework.lang.NonNull;

import java.time.Duration;

/**
 * @author K
 * @date 2021/2/4  15:15
 */
public class CacheHashKey extends CacheKey {
    @NonNull
    private Object field;

    public CacheHashKey() {
    }

    public CacheHashKey(@NonNull String key, @NonNull Object field) {
        super(key);
        this.field = field;
    }

    public CacheHashKey(@NonNull String key, @NonNull Object field, Duration expire) {
        super(key, expire);
        this.field = field;
    }

    public CacheKey tran() {
        return new CacheHashKey(StrUtil.join("", this.getKey(), this.getField()), this.getExpire());
    }

    @NonNull
    public Object getField() {
        return this.field;
    }

    public void setField(@NonNull Object field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "CacheHashKey(super=" + super.toString() + ", field=" + this.getField() + ")";
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof CacheHashKey;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CacheHashKey)) {
            return false;
        } else {
            CacheHashKey other = (CacheHashKey)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                Object thisField = this.getField();
                Object otherField = other.getField();
                if (thisField == null) {
                    if (otherField != null) {
                        return false;
                    }
                } else if (!thisField.equals(otherField)) {
                    return false;
                }

                return true;
            }
        }
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        Object field = this.getField();
        result = result * 59 + field.hashCode();
        return result;
    }

}
