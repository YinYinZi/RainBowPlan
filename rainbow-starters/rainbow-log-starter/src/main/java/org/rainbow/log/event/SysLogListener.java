package org.rainbow.log.event;

import org.rainbow.core.log.entity.OptLogDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 系统操作日志监听器
 *
 * @author K
 * @date 2021/2/9  9:54
 */
public class SysLogListener {
    Object target;
    private static final Logger log = LoggerFactory.getLogger(SysLogListener.class);
    private final Consumer<OptLogDTO> consumer;

    @Async
    @Order
    @EventListener({SysLogEvent.class})
    public void saveSysLog(SysLogEvent event) {
        OptLogDTO optLogDTO = (OptLogDTO) event.getSource();
        if (Objects.nonNull(optLogDTO)) {
            this.consumer.accept(optLogDTO);
        }
    }


    public SysLogListener(Consumer<OptLogDTO> consumer) {
        this.consumer = consumer;
    }
}
