package org.rainbow.database.datasource;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.BlockAttackSqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.rainbow.database.mybatis.WriteInterceptor;
import org.rainbow.database.properties.DatabaseProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Mybatis 常用重用拦截器
 * <p>
 * 拦截器执行一定是：
 * WriteInterceptor > DataScopeInterceptor > PaginationInterceptor
 *
 * @author K
 * @date 2021/1/26  18:46
 */
public class BaseMybatisConfiguration {
    protected final DatabaseProperties databaseProperties;

    public BaseMybatisConfiguration(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    /**
     * 演示环境权限拦截器
     */
    @Bean
    @Order(15)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "rainbow.database.isNotWrite", havingValue = "true")
    public WriteInterceptor writeInterceptor() {
        return new WriteInterceptor();
    }

    /**
     * 分页插件
     */
    @Bean
    @Order(5)
    @ConditionalOnMissingBean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        List<ISqlParser> sqlParserList = new ArrayList<>();

        // 判断是否开启攻击SQL阻断
        if (this.databaseProperties.isBlockAttack) {
            sqlParserList.add(new BlockAttackSqlParser());
        }

        paginationInterceptor.setSqlParserList(sqlParserList);
        return paginationInterceptor;
    }


}
