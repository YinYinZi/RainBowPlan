package org.rainbow.log.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.rainbow.core.log.annotation.SysLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 日志工具类
 *
 * @author K
 * @date 2021/2/9  10:42
 */
public final class LogUtil {
    private static final Logger log = LoggerFactory.getLogger(LogUtil.class);

    public static String getDescribe(JoinPoint joinPoint) {
        SysLog annotation = getTargetAnnotation(joinPoint);
        return annotation == null ? "" : annotation.value();
    }

    public static String getDescribe(SysLog annotation) {
        return annotation == null ? "" : annotation.value();
    }

    public static SysLog getTargetAnnotation(JoinPoint point) {
        try {
            SysLog annotation = null;
            if (point.getSignature() instanceof MethodSignature) {
                Method method = ((MethodSignature) point.getSignature()).getMethod();
                if (method != null) {
                    annotation = method.getAnnotation(SysLog.class);
                }
            }
            return annotation;
        } catch (Exception e) {
            log.warn("获取{}.{} 的 @SysLog注解失败,", new Object[]{point.getSignature().getDeclaringTypeName(), point.getSignature().getName()}, e);
            return null;
        }
    }


    private LogUtil() {

    }
}
