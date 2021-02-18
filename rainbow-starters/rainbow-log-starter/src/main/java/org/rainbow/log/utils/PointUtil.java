package org.rainbow.log.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author K
 * @date 2021/2/9  11:01
 */
public final class PointUtil {
    private static final Logger log = LoggerFactory.getLogger(PointUtil.class);
    private static final String MSG_PATTERN = "{}|{}|{}";


    private PointUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void info(String id, String type, String message) {
        log.info(MSG_PATTERN, id, type, message);
    }

    public static void debug(String id, String type, String message) {
        log.debug(MSG_PATTERN, id, type, message);
    }
}
