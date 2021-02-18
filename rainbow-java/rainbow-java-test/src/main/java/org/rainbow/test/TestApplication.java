package org.rainbow.test;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.rainbow.core.log.entity.OptLogDTO;
import org.rainbow.log.event.SysLogEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.Objects;

/**
 * @author K
 * @date 2021/2/6  11:09
 */
@ComponentScan({
    "cn.hutool", "org.rainbow"
})
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class);
    }

    @Async
    @Order
    @EventListener({SysLogEvent.class})
    public void saveSysLog(SysLogEvent event) {
        OptLogDTO optLogDTO = (OptLogDTO) event.getSource();
        System.out.println("处理2" + optLogDTO.toString());
    }
}
