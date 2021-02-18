package org.rainbow.log;

import org.rainbow.core.jackson.JsonUtil;
import org.rainbow.log.aspect.SysLogAspect;
import org.rainbow.log.event.SysLogListener;
import org.rainbow.log.utils.PointUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author K
 * @date 2021/2/14  12:39
 */
@EnableAsync
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(
        prefix = "rainbow.log",
        name = {"enable"},
        havingValue = "true",
        matchIfMissing = true
)
public class LogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SysLogAspect sysLogAspect() {
        return new SysLogAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("${rainbow.log.enable:true} && 'LOGGER'.equals('${rainbow.log.type:LOGGER}')")
    public SysLogListener sysLogListener() {
        return new SysLogListener((log) -> {
            PointUtil.debug("0", "OPT_LOG", JsonUtil.toJson(log));
        });
    }

    public LogAutoConfiguration() {

    }
}
