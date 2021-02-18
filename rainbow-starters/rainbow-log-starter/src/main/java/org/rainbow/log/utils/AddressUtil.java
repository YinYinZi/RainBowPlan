package org.rainbow.log.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.io.FileUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * 地址工具类
 *
 * @author K
 * @date 2021/2/9  10:19
 */
public final class AddressUtil {
    private static final Logger log = LoggerFactory.getLogger(AddressUtil.class);
    private static DbConfig config = null;
    private static final String JAVA_TEMP_DIR = "java.io.tmpdir";
    private static DbSearcher searcher = null;

    public static String getRegion(String ip) {
        try {
            if (Objects.nonNull(searcher) && StrUtil.isNotBlank(ip)) {
                long startTime = System.currentTimeMillis();
                Method method = searcher.getClass().getMethod("memorySearch", String.class);
                if (!Util.isIpAddress(ip)) {
                    log.warn("warning: invalid ip address {}", ip);
                }

                DataBlock dataBlock = (DataBlock) method.invoke(searcher, ip);
                String result = dataBlock != null ? dataBlock.getRegion() : "";
                long endTime = System.currentTimeMillis();
                log.debug("region use time[{}] result[{}]", endTime - startTime, result);
                return result;
            } else {
                log.error("DbSearcher is null");
                return "";
            }
        } catch (Exception e) {
            log.error("根据ip查询地区失败！", e);
            return "";
        }
    }

    static {
        try {
            String dbPath = AddressUtil.class.getResource("/ip2region/ip2region.db").getPath();
            File file = new File(dbPath);
            if (!file.exists()) {
                String tmpDir = System.getProperties().getProperty(JAVA_TEMP_DIR);
                dbPath = tmpDir + "ip2region.db";
                file = new File(dbPath);
                String classPath = "classpath:ip2region/ip2region.db";
                InputStream inputStream = ResourceUtil.getStreamSafe(classPath);
                if (Objects.nonNull(inputStream)) {
                    FileUtils.copyInputStreamToFile(inputStream, file);
                }
            }

            config = new DbConfig();
            searcher = new DbSearcher(config, dbPath);
            log.info("bean [{}]", config.toString());
            log.info("bean [{}]", searcher.toString());
        } catch (Exception e) {
            log.error("init ip region error", e);
        }
    }

    private AddressUtil() {
    }
}
