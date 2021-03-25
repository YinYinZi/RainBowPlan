package org.rainbow.xss.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.owasp.validator.html.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * XSS工具类 用于过滤特殊字符
 *
 * @author K
 * @date 2021/3/25  14:38
 */
public class XssUtils {
    private static final Logger log = LoggerFactory.getLogger(XssUtils.class);
    private static final String ANTISAMY_SLASHDOT_XML = "antisamy-slashdot-1.4.4.xml";
    private static Policy policy = null;

    static {
        log.debug(" start read XSS configFile [" + ANTISAMY_SLASHDOT_XML + "]");
        InputStream inputStream = XssUtils.class.getClassLoader().getResourceAsStream(ANTISAMY_SLASHDOT_XML);

        try {
            policy = Policy.getInstance(inputStream);
            log.debug("read Xss configFile [ " + ANTISAMY_SLASHDOT_XML + " ] success");
        } catch (PolicyException e) {
            log.error("read Xss configFile [ " + ANTISAMY_SLASHDOT_XML + " ] fail, reason:", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("close XSS configFile [" + ANTISAMY_SLASHDOT_XML + "] fail , reason:", e);
                }
            }
        }
    }

    /**
     * 跨站攻击语句过滤方法
     *
     * @param paramValue           待过滤的参数
     * @param ignoreParamValueList 忽略过滤的参数列表
     * @return 过滤后的字符串
     */
    public static String xssClean(String paramValue, List<String> ignoreParamValueList) {
        AntiSamy antiSamy = new AntiSamy();

        try {
            log.debug("raw value before xssClean: " + paramValue);
            if (isIgnoreParamValue(paramValue, ignoreParamValueList)) {
                log.debug("ignore the xssClean,keep the raw paramValue: " + paramValue);
                return paramValue;
            } else {
                final CleanResults cr = antiSamy.scan(paramValue, policy);
                cr.getErrorMessages().forEach(log::debug);
                String str = cr.getCleanHTML();
                str = str.replaceAll("&quot;", "\"");
                str = str.replaceAll("&amp;", "&");
                str = str.replaceAll("'", "'");
                str = str.replaceAll("'", "＇");

                str = str.replaceAll("&lt;", "<");
                str = str.replaceAll("&gt;", ">");
                log.debug("xssfilter value after xssClean" + str);

                return str;
            }
        } catch (ScanException e) {
            log.error("scan failed armter is [" + paramValue + "]", e);
        } catch (PolicyException e) {
            log.error("antisamy convert failed  armter is [" + paramValue + "]", e);
        }
        return paramValue;
    }

    private static boolean isIgnoreParamValue(String paramValue, List<String> ignoreParamValueList) {
        if (StrUtil.isBlank(paramValue)) {
            return true;
        }
        if (CollUtil.isEmpty(ignoreParamValueList)) {
            return false;
        } else {
            for (String ignoreParamValue : ignoreParamValueList) {
                if (ignoreParamValue.contains(paramValue)) {
                    return true;
                }
            }
        }
        return false;
    }

}