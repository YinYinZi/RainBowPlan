package org.rainbow.cache;

import com.google.common.collect.Maps;
import org.rainbow.cache.lock.RedisDistributeLook;
import org.rainbow.cache.properties.CacheType;
import org.rainbow.cache.properties.CustomCacheProperties;
import org.rainbow.cache.redis.RedisOps;
import org.rainbow.cache.repository.CacheOps;
import org.rainbow.cache.repository.impl.RedisOpsImpl;
import org.rainbow.cache.utils.RedisObjectSerializer;
import org.rainbow.core.lock.DistributedLock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Redis自动配置类
 *
 * @author K
 * @date 2021/2/5  18:01
 */
@ConditionalOnClass({RedisConnectionFactory.class})
@ConditionalOnProperty(
        name = {"rainbow.cache.type"},
        havingValue = "REDIS",
        matchIfMissing = true
)
@EnableConfigurationProperties({RedisProperties.class, CustomCacheProperties.class})
public class RedisAutoConfigure {
    private final CustomCacheProperties cacheProperties;

    public RedisAutoConfigure(CustomCacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    @Bean({"redisTemplate"})
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        this.setSerializer(factory, template);
        return template;
    }

    @Bean({"stringRedisTemplate"})
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        this.setSerializer(factory, template);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public DistributedLock redisDistributedLock(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate) {
        return new RedisDistributeLook(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheOps cacheOps(RedisOps redisOps) {
        return new RedisOpsImpl(redisOps);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisOps getRedisOps(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate) {
        return new RedisOps(redisTemplate, this.cacheProperties.getCacheNullVal());
    }

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defConfig = this.getDefConf();
        defConfig.entryTtl(this.cacheProperties.getDef().getTimeToLive());
        Map<String, CustomCacheProperties.Cache> configs = this.cacheProperties.getConfigs();
        HashMap<String, RedisCacheConfiguration> map = Maps.newHashMap();
        Optional.ofNullable(configs).ifPresent((config) -> {
            config.forEach((key, cache) -> {
                RedisCacheConfiguration cfg = this.handleRedisCacheConfiguration(cache, defConfig);
                map.put(key, cfg);
            });
        });
        return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(defConfig)
                .withInitialCacheConfigurations(map).build();
    }

    private void setSerializer(RedisConnectionFactory factory, RedisTemplate template) {
        RedisObjectSerializer redisObjectSerializer = new RedisObjectSerializer();
        RedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(redisObjectSerializer);
        template.setValueSerializer(redisObjectSerializer);
        template.setConnectionFactory(factory);
    }

    private RedisCacheConfiguration getDefConf() {
        RedisCacheConfiguration def = RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new RedisObjectSerializer()));
        return this.handleRedisCacheConfiguration(this.cacheProperties.getDef(), def);
    }

    private RedisCacheConfiguration handleRedisCacheConfiguration(CustomCacheProperties.Cache redisProperties,
                                                                  RedisCacheConfiguration config) {
        if (Objects.isNull(redisProperties)) {
            return config;
        } else {
            if (redisProperties.getTimeToLive() != null) {
                config = config.entryTtl(redisProperties.getTimeToLive());
            }

            if (redisProperties.getKeyPrefix() != null) {
                config = config.computePrefixWith((cacheName) -> redisProperties.getKeyPrefix().concat(":").concat(cacheName).concat(":"));
            } else {
                config = config.computePrefixWith((cacheName) -> cacheName.concat(":"));
            }

            if (!redisProperties.isCacheNullValues()) {
                config = config.disableCachingNullValues();
            }

            if (!redisProperties.isUseKeyPrefix()) {
                config = config.disableKeyPrefix();
            }
            return config;
        }
    }
}
