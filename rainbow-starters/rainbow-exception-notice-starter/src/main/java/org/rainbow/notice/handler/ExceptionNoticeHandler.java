package org.rainbow.notice.handler;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.rainbow.notice.content.ExceptionInfo;
import org.rainbow.notice.process.INoticeProcessor;
import org.rainbow.notice.properties.ExceptionNoticeProperties;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * 异常信息通知前处理
 *
 * @author K
 * @date 2021/2/23  11:02
 */
@Slf4j
public class ExceptionNoticeHandler {
    private final String SEPARATOR = System.getProperty("line.separator");

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private final BlockingQueue<ExceptionInfo> exceptionInfoBlockingQueue = new ArrayBlockingQueue<>(1024);

    private final ExceptionNoticeProperties exceptionNoticeProperties;

    private final List<INoticeProcessor> noticeProcessors;

    public ExceptionNoticeHandler(ExceptionNoticeProperties exceptionProperties,
                                  List<INoticeProcessor> noticeProcessors) {
        this.exceptionNoticeProperties = exceptionProperties;
        this.noticeProcessors = noticeProcessors;
    }

    /**
     * 将捕获的异常封装成ExceptionInfo塞入阻塞队列
     *
     * @param ex 异常
     * @param joinPoint 切点
     * @return true / false
     */
    public Boolean createNotice(Exception ex, JoinPoint joinPoint) {
        if (containsException(ex)) {
            return null;
        }
        log.error("捕获到异常开始发送消息通知:{}method:{}--->", SEPARATOR, joinPoint.getSignature().getName());
        // 获取请求参数
        Object parameter = getParameter(joinPoint);
        // 获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String address = null;
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            //获取请求地址
            address = request.getRequestURL().toString() + ((request.getQueryString() != null && request.getQueryString().length() > 0) ? "?" + request.getQueryString() : "");
        }
        ExceptionInfo exceptionInfo = new ExceptionInfo(ex, joinPoint.getSignature().getName(), exceptionNoticeProperties.getIncludedTracePackage(), parameter, address);
        exceptionInfo.setProject(exceptionNoticeProperties.getProjectName());
        return exceptionInfoBlockingQueue.offer(exceptionInfo);
    }

    public void start() {
        executor.scheduleAtFixedRate(() -> {
            ExceptionInfo exceptionInfo = exceptionInfoBlockingQueue.poll();
            if (null != exceptionInfo) {
                noticeProcessors.forEach(processor -> processor.sendNotice(exceptionInfo));
            }
        }, 6, exceptionNoticeProperties.getPeriod(), TimeUnit.SECONDS);
    }

    private boolean containsException(Exception exception) {
        Class<? extends Exception> exceptionClass = exception.getClass();
        List<Class<? extends Exception>> list = exceptionNoticeProperties.getExcludeExceptions();
        for (Class<? extends Exception> clazz : list) {
            if (clazz.isAssignableFrom(exceptionClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据方法和传入的参数获取请求参数
     *
     * @param joinPoint 切点
     * @return 请求参数对象
     */
    private Object getParameter(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Parameter[] parameters = method.getParameters();

        Object[] args = joinPoint.getArgs();
        List<Object> argsList = new ArrayList<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                argsList.add(args[i]);
            }
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null) {
                HashMap<String, Object> map = new HashMap<>(1);
                String key = parameters[i].getName();
                if (!StringUtils.isEmpty(requestParam.value())) {
                    key = requestParam.value();
                }
                map.put(key, args[i]);
                argsList.add(map);
            }
        }
        if (argsList.size() == 0) {
            return null;
        } else if (argsList.size() == 1) {
            return argsList.get(0);
        } else {
            return argsList;
        }
    }
}
