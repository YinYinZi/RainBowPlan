package org.rainbow.notice.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.rainbow.notice.handler.ExceptionNoticeHandler;

/**
 * 异常捕获切面
 *
 * @author K
 * @date 2021/2/23  11:42
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
public class ExceptionListener {

    private final ExceptionNoticeHandler handler;

    @AfterThrowing(value = "@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller) || @within(org.rainbow.notice.annotation.ExceptionNotice)", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, Exception e) {
        handler.createNotice(e, joinPoint);
    }
}
