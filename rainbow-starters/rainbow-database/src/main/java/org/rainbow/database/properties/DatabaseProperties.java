package org.rainbow.database.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.rainbow.database.properties.DatabaseProperties.PREFIX;

/**
 * 数据库配置属性
 *
 * @author K
 * @date 2021/1/26  15:47
 */
@ConfigurationProperties(prefix = PREFIX)
@Data
@NoArgsConstructor
public class DatabaseProperties {
    public static final String PREFIX = "rainbow.database";

    /**
     * 攻击SQL阻断解析器
     */
    public Boolean isBlockAttack = false;
    /**
     * 是否开启seata
     */
    public Boolean isSeata = false;
    /**
     * 是否禁止写入
     */
    private Boolean isNotWrite = false;
    /**
     * 是否开启数据权限
     */
    private Boolean isDataScope = true;
    /**
     * 事务超时时间 60 * 60
     */
    private int txTimeout = 60 * 60;
    /**
     * Id生成策略
     */
    private Id id = new Id();
    /**
     * 统一管理事务的方法名
     */
    private List<String> transactionAttributeList = new ArrayList<>(Arrays.asList("add*", "save*", "insert*",
            "create*", "update*", "edit*", "upload*", "delete*", "remove*",
            "clean*", "recycle*", "batch*", "mark*", "disable*", "enable*", "handle*", "syn*",
            "reg*", "gen*", "*Tx"
    ));
    /**
     * 事务扫描基础包
     */
    private String transactionScanPackage = "org.rainbow";

    @Data
    public static class Id {
        /**
         * 终端ID (0-31)      单机配置0 即可。 集群部署，根据情况每个实例自增即可。
         */
        private Long workerId = 0L;
        /**
         * 数据中心ID (0-31)   单机配置0 即可。 集群部署，根据情况每个实例自增即可。
         */
        private Long dataCenterId = 0L;
    }
}
