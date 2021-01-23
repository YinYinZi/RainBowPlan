package org.rainbow.rabbitmq.producer.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author K
 * @date 2021/1/22  16:28
 */
@Configuration
@EnableTransactionManagement
@Slf4j
public class DruidDataSourceConfig {

    @Autowired
    private DruidDataSourceSettings druidDataSourceSettings;

    public static String DRIVER_CLASS;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(druidDataSourceSettings.getDriverClassName());
        DRIVER_CLASS = druidDataSourceSettings.getDriverClassName();
        ds.setUrl(druidDataSourceSettings.getUrl());
        ds.setUsername(druidDataSourceSettings.getUsername());
        ds.setPassword(druidDataSourceSettings.getPassword());
        ds.setInitialSize(druidDataSourceSettings.getInitialSize());
        ds.setMinIdle(druidDataSourceSettings.getMinIdle());
        ds.setMaxActive(druidDataSourceSettings.getMaxActive());
        ds.setTimeBetweenEvictionRunsMillis(druidDataSourceSettings.getTimeBetweenEvictionRunsMillis());
        ds.setMinEvictableIdleTimeMillis(druidDataSourceSettings.getMinEvictableIdleTimeMillis());
        ds.setValidationQuery(druidDataSourceSettings.getValidationQuery());
        ds.setTestWhileIdle(druidDataSourceSettings.isTestWhileIdle());
        ds.setTestOnBorrow(druidDataSourceSettings.isTestOnBorrow());
        ds.setTestOnReturn(druidDataSourceSettings.isTestOnReturn());
        ds.setPoolPreparedStatements(druidDataSourceSettings.isPoolPreparedStatements());
        ds.setMaxPoolPreparedStatementPerConnectionSize(druidDataSourceSettings.getMaxPoolPreparedStatementPerConnectionSize());
        ds.setFilters(druidDataSourceSettings.getFilters());
        ds.setConnectionProperties(druidDataSourceSettings.getConnectionProperties());
        log.info(" druid datasource config : {} ", ds);
        return ds;
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource());
        return dataSourceTransactionManager;
    }
}
