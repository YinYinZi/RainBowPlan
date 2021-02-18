package org.rainbow.log.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author K
 * @date 2021/2/9  9:38
 */
public class SysLogEvent extends ApplicationEvent {

    public SysLogEvent(Object source) {
        super(source);
    }
}
