package org.rainbow.test.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rainbow.cache.repository.CacheOps;
import org.rainbow.core.cache.CacheKey;
import org.rainbow.core.log.annotation.SysLog;
import org.rainbow.dingtalk.sdk.DingTalkSender;
import org.rainbow.dingtalk.sdk.message.DingTalkLinkMessage;
import org.rainbow.dingtalk.sdk.message.DingTalkMessage;
import org.rainbow.dingtalk.sdk.message.DingTalkTextMessage;
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
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private CacheOps cacheOps;
    @Resource
    private DingTalkSender dingTalkSender;

    @SysLog(value = "test接口")
    @GetMapping
    public String test() {
        log.debug("test");
        CacheKey cacheKey = new CacheKey();
        cacheKey.setKey("test");
        cacheKey.setExpire(Duration.ofHours(1));
        cacheOps.set(cacheKey, "caonima", false);

        DingTalkTextMessage dingTalkTextMessage = new DingTalkTextMessage();
        dingTalkTextMessage.setContent("告警:数据库执行删库操作");
        dingTalkSender.sendSecretMessage(dingTalkTextMessage);
        return cacheKey.toString();
    }
}
