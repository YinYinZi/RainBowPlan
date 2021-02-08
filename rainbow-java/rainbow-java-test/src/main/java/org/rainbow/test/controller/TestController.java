package org.rainbow.test.controller;

import lombok.AllArgsConstructor;
import org.rainbow.cache.repository.CacheOps;
import org.rainbow.core.cache.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * @author K
 * @date 2021/2/6  11:17
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private CacheOps cacheOps;

    @GetMapping
    public String test() {
        CacheKey cacheKey = new CacheKey();
        cacheKey.setKey("test");
        cacheKey.setExpire(Duration.ofHours(1));
        cacheOps.set(cacheKey, "caonima", false);
        return cacheKey.toString();
    }
}
