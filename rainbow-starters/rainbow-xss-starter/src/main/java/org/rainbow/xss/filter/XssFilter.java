package org.rainbow.xss.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.rainbow.xss.wrapper.XssRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 跨站过滤器
 *
 * @author K
 * @date 2021/3/25  13:40
 */
@Slf4j
public class XssFilter implements Filter {

    /**
     * 可放行的请求路径
     */
    private static final String IGNORE_PATH = "ignorePath";

    /**
     * 可放行的参数值
     */
    private static final String IGNORE_PARAM_VALUE = "ignoreParamValue";

    /**
     * 默认放行的单点登录的登出响应(响应中包含samlp:LogoutRequest标签，直接放行)
     */
    private static final String CAS_LOGOUT_RESPONSE_TAG = "sample:LogoutRequest";

    /**
     * 可放行的请求路径列表
     */
    private List<String> ignorePathList;

    /**
     * 可放行的参数值列表
     */
    private List<String> ignoreParamValueList;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("XSS Filter [XssFilter] init start...");
        String ignorePaths = filterConfig.getInitParameter(IGNORE_PATH);
        String ignoreParamValues = filterConfig.getInitParameter(IGNORE_PARAM_VALUE);
        if (!StrUtil.isBlank(ignorePaths)) {
            String[] ignorePathArr = ignorePaths.split(",");
            ignorePathList = Arrays.asList(ignorePathArr);
        }
        if (!StrUtil.isBlank(ignoreParamValues)) {
            String[] ignoreParamValueArr = ignoreParamValues.split(",");
            ignoreParamValueList = Arrays.asList(ignoreParamValueArr);
            // 默认放行单点登录的登出响应(响应中包含samlp:LogoutRequest标签，直接放行)
            if (!ignoreParamValueList.contains(CAS_LOGOUT_RESPONSE_TAG)) {
                ignoreParamValueList.add(CAS_LOGOUT_RESPONSE_TAG);
            }
        } else {
            //默认放行单点登录的登出响应(响应中包含samlp:LogoutRequest标签，直接放行)
            ignoreParamValueList = new ArrayList<String>();
            ignoreParamValueList.add(CAS_LOGOUT_RESPONSE_TAG);
        }
        log.debug("ignorePathList=" + JSON.toJSONString(ignorePathList));
        log.debug("ignoreParamValueList=" + JSON.toJSONString(ignoreParamValueList));
        log.debug("XSS fiter [XSSFilter] init end");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("Xss Filter [XssFilter] starting");
        // 判断uri是否包含项目名称
        String uriPath = ((HttpServletRequest) request).getRequestURI();
        if (isIgnorePath(uriPath)) {
            log.debug("ignore xssFilter, path[" + uriPath + "] pass through XssFilter, go ahead...");
            chain.doFilter(request, response);
            return;
        } else {
            log.debug("has xssFiter path[" + uriPath + "] need XssFilter, go to XssRequestWrapper");
            chain.doFilter(new XssRequestWrapper((HttpServletRequest) request, ignoreParamValueList), response);
        }
        log.debug("Xss Filter [XssFilter] stop");
    }

    @Override
    public void destroy() {
        log.debug("XSS Filter [XSSFilter] destroy");
    }

    private boolean isIgnorePath(String servletPath) {
        if (StrUtil.isBlank(servletPath)) {
            return true;
        }
        if (CollUtil.isEmpty(ignorePathList)) {
            return false;
        } else {
            for (String ignorePath : ignorePathList) {
                if (StrUtil.isNotBlank(ignorePath) && servletPath.contains(ignorePath.trim())) {
                    return true;
                }
            }
        }

        return false;
    }
}
