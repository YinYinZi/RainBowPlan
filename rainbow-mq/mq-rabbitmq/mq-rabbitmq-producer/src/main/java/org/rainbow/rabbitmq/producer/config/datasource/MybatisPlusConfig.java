package org.rainbow.rabbitmq.producer.config.datasource;

import com.baomidou.mybatisplus.annotation.DbType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author K
 * @date 2021/1/23  10:03
 */
@Configuration
@MapperScan("org.rainbow.rabbitmq.producer.mapper")
public class MybatisPlusConfig {

   /* @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }*/

}
