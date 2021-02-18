package org.rainbow.dingtalk.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author K
 * @date 2021/2/18  14:49
 */
@Data
@ConfigurationProperties(prefix = "rainbow.dingtalk")
public class DingTalkProperties {

    /**
     * web hook地址
     */
    private String url;

    /**
     * 密钥
     */
    private String secret;
}
