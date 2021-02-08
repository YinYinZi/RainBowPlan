package org.rainbow.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * 缓存自动配置
 *
 * @author K
 * @date 2021/2/6  10:27
 */
@EnableCaching
@Import({RedisAutoConfigure.class, CaffeineAutoConfigure.class})
public class CacheAutoConfigure {
    private static final Logger log = LoggerFactory.getLogger(CacheAutoConfigure.class);

    public CacheAutoConfigure() {}

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(":");
            sb.append(method.getName());
            Object[] var4 = objects;
            int var5 = objects.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Object obj = var4[var6];
                if (obj != null) {
                    sb.append(":");
                    sb.append(obj.toString());
                }
            }
            return sb.toString();
        };
    }
}
