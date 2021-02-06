package org.rainbow.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.rainbow.cache.lock.CaffeineDistributedLock;
import org.rainbow.cache.properties.CustomCacheProperties;
import org.rainbow.cache.repository.CacheOps;
import org.rainbow.cache.repository.impl.CaffeineOpsImpl;
import org.rainbow.core.lock.DistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Caffeine自动配置类
 *
 * @author K
 * @date 2021/2/6  10:16
 */
@ConditionalOnProperty(
        prefix = "rainbow.cache",
        name = {"type"},
        havingValue = "CAFFEINE"
)
@EnableConfigurationProperties({CustomCacheProperties.class})
public class CaffeineAutoConfigure {
    private static final Logger log = LoggerFactory.getLogger(CaffeineAutoConfigure.class);
    private final CustomCacheProperties cacheProperties;

    @Bean
    @ConditionalOnMissingBean
    public DistributedLock caffeineDistributedLock() {
        return new CaffeineDistributedLock();
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheOps cacheOps() {
        return new CaffeineOpsImpl();
    }

    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine caffeine = Caffeine.newBuilder().recordStats().initialCapacity(500)
                .expireAfterWrite(this.cacheProperties.getDef().getTimeToLive())
                .maximumSize(this.cacheProperties.getDef().getMaxSize());
        caffeineCacheManager.setAllowNullValues(this.cacheProperties.getDef().isCacheNullValues());
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    public CaffeineAutoConfigure(CustomCacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }
}
