package org.rainbow.log.aspect;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.swagger.annotations.Api;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.rainbow.core.base.R;
import org.rainbow.core.context.BaseContextHandler;
import org.rainbow.core.context.ThreadLocalParam;
import org.rainbow.core.jackson.JsonUtil;
import org.rainbow.core.log.annotation.SysLog;
import org.rainbow.core.log.entity.OptLogDTO;
import org.rainbow.log.event.SysLogEvent;
import org.rainbow.log.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author K
 * @date 2021/2/9  11:11
 */
@Aspect
public class SysLogAspect {
    private static final Logger log = LoggerFactory.getLogger(SysLogAspect.class);
    public static final int MAX_LENGTH = 65535;
    private static final ThreadLocal<OptLogDTO> THREAD_LOCAL = new ThreadLocal<>();
    private static final String FROM_DATA_CONTENT_TYPE = "multipart/form-data";
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("@annotation(org.rainbow.core.log.annotation.SysLog)")
    public void sysLogAspect() {

    }

    @AfterReturning(
            returning = "ret",
            pointcut = "sysLogAspect()"
    )
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        this.tryCatch((p) -> {
            SysLog sysLog = LogUtil.getTargetAnnotation(joinPoint);
            if (!this.check(joinPoint, sysLog)) {
                R r = Convert.convert(R.class, ret);
                OptLogDTO sysLogDTO = this.get();
                if (r == null) {
                    sysLogDTO.setType("OPT");
                    if (sysLog.response()) {
                        sysLogDTO.setResult(this.getText(String.valueOf(ret == null ? "" : ret)));
                    }
                } else {
                    if (r.getIsSuccess()) {
                        sysLogDTO.setType("OPT");
                    } else {
                        sysLogDTO.setType("EX");
                        sysLogDTO.setExDetail(r.getMsg());
                    }

                    if (sysLog.response()) {
                        sysLogDTO.setResult(this.getText(r.toString()));
                    }
                }

                this.publishEvent(sysLogDTO);
            }
        });
    }

    @AfterThrowing(
            throwing = "e",
            pointcut = "sysLogAspect()"
    )
    public void doAfterThrowable(JoinPoint joinPoint, Throwable e) {
        this.tryCatch((val) -> {
            SysLog sysLog = LogUtil.getTargetAnnotation(joinPoint);
            if (!this.check(joinPoint, sysLog)) {
                OptLogDTO optLogDTO = this.get();
                optLogDTO.setType("EX");
                if (!sysLog.request() && sysLog.requestByError() && StrUtil.isEmpty(optLogDTO.getParams())) {
                    Object[] args = joinPoint.getArgs();
                    HttpServletRequest request = ((ServletRequestAttributes)Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                    String strArgs = this.getArgs(args, request);
                    optLogDTO.setParams(this.getText(strArgs));
                }

                optLogDTO.setExDetail(ExceptionUtil.stacktraceToString(e, 65535));
                this.publishEvent(optLogDTO);
            }
        });
    }

    @Before("sysLogAspect()")
    public void doBefore(JoinPoint joinPoint) {
        this.tryCatch((val) -> {
            SysLog sysLog = LogUtil.getTargetAnnotation(joinPoint);
            if (!this.check(joinPoint, sysLog)) {
                OptLogDTO optLogDTO = this.buildOptLogDTO(joinPoint, sysLog);
                THREAD_LOCAL.set(optLogDTO);
            }
        });
    }

    @NonNull
    private OptLogDTO buildOptLogDTO(JoinPoint joinPoint, SysLog sysLog) {
        OptLogDTO optLogDTO = this.get();
        optLogDTO.setCreateBy(BaseContextHandler.getUserId());
        optLogDTO.setUserName(BaseContextHandler.getName());
        this.setDescription(joinPoint, sysLog, optLogDTO);
        optLogDTO.setClassPath(joinPoint.getTarget().getClass().getName());
        optLogDTO.setActionMethod(joinPoint.getSignature().getName());
        HttpServletRequest request = this.setParams(joinPoint, sysLog, optLogDTO);
        optLogDTO.setRequestIp(ServletUtil.getClientIP(request));
        optLogDTO.setRequestUri(URLUtil.getPath(request.getRequestURI()));
        optLogDTO.setHttpMethod(request.getMethod());
        optLogDTO.setUa(StrUtil.sub(request.getHeader("user-agent"), 0, 500));
        optLogDTO.setTrace(MDC.get("trace"));
        if (StrUtil.isEmpty(optLogDTO.getTrace())) {
            optLogDTO.setTrace(request.getHeader("x-trace-header"));
        }
        optLogDTO.setStartTime(LocalDateTime.now());
        return optLogDTO;
    }

    @NotNull
    private HttpServletRequest setParams(JoinPoint joinPoint, SysLog sysLog, OptLogDTO optLogDTO) {
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes(), "只能在Spring Web环境使用@SysLog记录日志")).getRequest();
        if (sysLog.request()) {
            String strArgs = this.getArgs(args, request);
            optLogDTO.setParams(this.getText(strArgs));
        }
        return request;
    }

    private void setDescription(JoinPoint joinPoint, SysLog sysLog, OptLogDTO optLogDTO) {
        String controllerDescription = "";
        Api api = joinPoint.getTarget().getClass().getAnnotation(Api.class);
        if (api != null) {
            String[] tags = api.tags();
            if (ArrayUtil.isNotEmpty(tags)) {
                controllerDescription = tags[0];
            }
        }

        String controllerMethodDescription = LogUtil.getDescribe(sysLog);
        if (StrUtil.isNotEmpty(controllerMethodDescription) && StrUtil.contains(controllerMethodDescription, "#")) {
            Object[] args = joinPoint.getArgs();
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            controllerMethodDescription = this.getValBySpEl(controllerMethodDescription, methodSignature, args);
        }

        optLogDTO.setDescription(controllerMethodDescription);

    }

    private OptLogDTO get() {
        OptLogDTO sysLog = THREAD_LOCAL.get();
        return sysLog == null ? new OptLogDTO() : sysLog;
    }

    private void tryCatch(Consumer<String> consumer) {
        try {
            consumer.accept("");
        } catch (Exception e) {
            log.warn("记录操作日志异常", e);
            THREAD_LOCAL.remove();
        }
    }

    private void publishEvent(OptLogDTO sysLog) {
        sysLog.setFinishTime(LocalDateTime.now());
        sysLog.setConsumingTime(sysLog.getStartTime().until(sysLog.getFinishTime(), ChronoUnit.MILLIS));
        SpringUtil.getApplicationContext().publishEvent(new SysLogEvent(sysLog));
        THREAD_LOCAL.remove();
    }

    private boolean check(JoinPoint joinPoint, SysLog sysLog) {
        if (sysLog != null && sysLog.enabled()) {
            SysLog targetClass = joinPoint.getTarget().getClass().getAnnotation(SysLog.class);
            return targetClass != null && !targetClass.enabled();
        } else {
            return true;
        }
    }

    private String getText(String val) {
        return StrUtil.sub(val, 0, 65535);
    }

    private String getArgs(Object[] args, HttpServletRequest request) {
        String strArgs = "";
        try {
            if (!request.getContentType().contains(FROM_DATA_CONTENT_TYPE)) {
                strArgs = JsonUtil.toJson(args);
            }
        } catch (Exception e) {
            try {
                strArgs = Arrays.toString(args);
            } catch (Exception ex) {
                log.warn("解析参数异常", ex);
            }
        }
        return strArgs;
    }

    private String getValBySpEl(String spEl, MethodSignature methodSignature, Object[] args) {
        try {
            String[] paramNames = this.nameDiscoverer.getParameterNames(methodSignature.getMethod());
            if (paramNames != null && paramNames.length > 0) {
                Expression expression = this.spelExpressionParser.parseExpression(spEl);
                StandardEvaluationContext context = new StandardEvaluationContext();

                for (int i = 0; i < args.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                    context.setVariable("p" + i, args[i]);
                }

                ThreadLocalParam tlp = new ThreadLocalParam();
                BeanUtil.fillBeanWithMap(BaseContextHandler.getLocalMap(), tlp, true);
                context.setVariable("threadLocal", tlp);
                Object value = expression.getValue(context);
                return value == null ? spEl : value.toString();
            }
        } catch (Exception e) {
            log.warn("解析日志的el表达式出错", e);
        }
        return spEl;
    }

    public SysLogAspect() {

    }
}
