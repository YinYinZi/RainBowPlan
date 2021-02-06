package org.rainbow.cache.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

/**
 * 自定义缓存Properties
 *
 * @author K
 * @date 2021/2/4  14:02
 */
@ConfigurationProperties(
        prefix = "rainbow.cache"
)
public class CustomCacheProperties {
    private static final String PREFIX = "rainbow.cache";
    private CacheType type;
    private Boolean cacheNullVal;
    private CustomCacheProperties.Cache def;
    private Map<String, CustomCacheProperties.Cache> configs;

    public CustomCacheProperties() {
        this.type = CacheType.REDIS;
        this.cacheNullVal = true;
        this.def = new CustomCacheProperties.Cache();
    }

    public CacheType getType() {
        return type;
    }

    public void setType(CacheType type) {
        this.type = type;
    }

    public Boolean getCacheNullVal() {
        return cacheNullVal;
    }

    public void setCacheNullVal(Boolean cacheNullVal) {
        this.cacheNullVal = cacheNullVal;
    }

    public Cache getDef() {
        return def;
    }

    public void setDef(Cache def) {
        this.def = def;
    }

    public Map<String, Cache> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, Cache> configs) {
        this.configs = configs;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CustomCacheProperties)) {
            return false;
        } else {
            CustomCacheProperties other = (CustomCacheProperties) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59:
                {
                    Object thisCacheNullVal = this.getCacheNullVal();
                    Object otherCacheNullVal = other.getCacheNullVal();
                    if (thisCacheNullVal == null) {
                        if (otherCacheNullVal == null) {
                            break label59;
                        }
                    } else if (thisCacheNullVal.equals(otherCacheNullVal)) {
                        break label59;
                    }

                    return false;
                }

                Object thisType = this.getType();
                Object otherType = other.getType();
                if (thisType == null) {
                    if (otherType != null) {
                        return false;
                    }
                } else if (!thisType.equals(otherType)) {
                    return false;
                }

                Object thisDef = this.getDef();
                Object otherDef = other.getDef();
                if (thisDef == null) {
                    if (otherDef != null) {
                        return false;
                    }
                } else if (!thisDef.equals(otherDef)) {
                    return false;
                }

                Object this$configs = this.getConfigs();
                Object other$configs = other.getConfigs();
                if (this$configs == null) {
                    if (other$configs != null) {
                        return false;
                    }
                } else if (!this$configs.equals(other$configs)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof CustomCacheProperties;
    }

    @Override
    public int hashCode() {
        int result = 1;
        Object cacheNullVal = this.getCacheNullVal();
        result = result * 59 + (cacheNullVal == null ? 43 : cacheNullVal.hashCode());
        Object $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Object $def = this.getDef();
        result = result * 59 + ($def == null ? 43 : $def.hashCode());
        Object $configs = this.getConfigs();
        result = result * 59 + ($configs == null ? 43 : $configs.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "CustomCacheProperties(type=" + this.getType() + ", cacheNullVal=" + this.getCacheNullVal() + ", def=" + this.getDef() + ", configs=" + this.getConfigs() + ")";
    }

    public static class Cache {
        private Duration timeToLive = Duration.ofDays(1);
        private boolean cacheNullValues = true;
        private String keyPrefix;
        private boolean useKeyPrefix = true;
        private int maxSize = 1000;

        public Cache() {
        }

        public Duration getTimeToLive() {
            return timeToLive;
        }

        public void setTimeToLive(Duration timeToLive) {
            this.timeToLive = timeToLive;
        }

        public boolean isCacheNullValues() {
            return cacheNullValues;
        }

        public void setCacheNullValues(boolean cacheNullValues) {
            this.cacheNullValues = cacheNullValues;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public boolean isUseKeyPrefix() {
            return useKeyPrefix;
        }

        public void setUseKeyPrefix(boolean useKeyPrefix) {
            this.useKeyPrefix = useKeyPrefix;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof CustomCacheProperties.Cache)) {
                return false;
            } else {
                CustomCacheProperties.Cache other = (CustomCacheProperties.Cache) o;
                if (!other.canEqual(this)) {
                    return false;
                } else if (this.isCacheNullValues() != other.isCacheNullValues()) {
                    return false;
                } else if (this.isUseKeyPrefix() != other.isUseKeyPrefix()) {
                    return false;
                } else if (this.getMaxSize() != other.getMaxSize()) {
                    return false;
                } else {
                    Duration thisTimeToLive = this.getTimeToLive();
                    Duration otherTimeToLive = other.getTimeToLive();
                    if (thisTimeToLive == null) {
                        if (otherTimeToLive != null) {
                            return false;
                        }
                    } else if (!thisTimeToLive.equals(otherTimeToLive)) {
                        return false;
                    }

                    String thisKeyPrefix = this.keyPrefix;
                    String otherKeyPrefix = other.getKeyPrefix();
                    if (thisKeyPrefix == null) {
                        if (otherKeyPrefix != null) {
                            return false;
                        }
                    } else if (!thisKeyPrefix.equals(otherKeyPrefix)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof CustomCacheProperties.Cache;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = result * 59 + (this.isCacheNullValues() ? 79 : 97);
            result = result * 59 + (this.isUseKeyPrefix() ? 79 : 97);
            result = result * 59 + this.getMaxSize();
            Object timeToLive = this.getTimeToLive();
            result = result * 59 + (timeToLive == null ? 43 : timeToLive.hashCode());
            Object $keyPrefix = this.getKeyPrefix();
            result = result * 59 + ($keyPrefix == null ? 43 : $keyPrefix.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return "CustomCacheProperties.Cache(timeToLive=" + this.getTimeToLive() + ", cacheNullValues=" + this.isCacheNullValues() + ", keyPrefix=" + this.getKeyPrefix() + ", useKeyPrefix=" + this.isUseKeyPrefix() + ", maxSize=" + this.getMaxSize() + ")";
        }
    }
}
