package org.rainbow.xss.wrapper;

import lombok.extern.slf4j.Slf4j;
import org.rainbow.xss.utils.XssUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.List;
import java.util.Map;

/**
 * 跨站攻击请求包装器
 *
 * @author K
 * @date 2021/3/25  14:25
 */
@Slf4j
public class XssRequestWrapper extends HttpServletRequestWrapper {

    private List<String> ignoreParamValueList;

    public XssRequestWrapper(HttpServletRequest request, List<String> ignoreParamValueList) {
        super(request);
        this.ignoreParamValueList = ignoreParamValueList;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> requestMap = super.getParameterMap();
        for (Map.Entry<String, String[]> me : requestMap.entrySet()) {
            log.debug(me.getKey() + ":");
            String[] values = me.getValue();
            for (int i = 0; i < values.length; i++) {
                log.debug(values[i]);
                values[i] = XssUtils.xssClean(values[i], this.ignoreParamValueList);
            }
        }
        return requestMap;
    }

    @Override
    public String[] getParameterValues(String paramString) {
        String[] parameterValues = super.getParameterValues(paramString);
        if (null == parameterValues) {
            return null;
        }
        int i = parameterValues.length;
        String[] resultArr = new String[i];
        for (int j = 0; j < i; j++) {
            resultArr[j] = XssUtils.xssClean(parameterValues[j], ignoreParamValueList);
        }
        return resultArr;
    }

    @Override
    public String getParameter(String paramString) {
        String str = super.getParameter(paramString);
        if (null == str) {
            return null;
        }
        return XssUtils.xssClean(str, ignoreParamValueList);
    }

    @Override
    public String getHeader(String paramString) {
        String str = super.getHeader(paramString);
        if (null == str) {
            return null;
        }
        return XssUtils.xssClean(str, ignoreParamValueList);
    }
}
