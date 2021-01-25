package org.rainbow.core.converter;

import lombok.extern.slf4j.Slf4j;
import org.rainbow.core.exception.BizException;
import org.springframework.core.convert.converter.Converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.rainbow.core.exception.BaseException.BASE_VALID_PARAM;
import static org.rainbow.core.utils.DateUtils.*;

/**
 * 解决入参为Date类型
 *
 * @author K
 * @date 2021/1/25  14:33
 */
@Slf4j
public class String2DateConverter extends BaseDateConverter<Date> implements Converter<String, Date> {

    protected static final Map<String, String> FORMAT = new LinkedHashMap<>(11);

    static {
        FORMAT.put(DEFAULT_YEAR_FORMAT, "^\\d{4}");
        FORMAT.put(DEFAULT_MONTH_FORMAT, "^\\d{4}-\\d{1,2}$");
        FORMAT.put(DEFAULT_DATE_FORMAT, "^\\d{4}-\\d{1,2}-\\d{1,2}$");
        FORMAT.put("yyyy-MM-dd HH", "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}");
        FORMAT.put("yyyy-MM-dd HH:mm", "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$");
        FORMAT.put(DEFAULT_DATE_TIME_FORMAT, "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$");
        FORMAT.put("yyyy/MM", "^\\d{4}/\\d{1,2}$");
        FORMAT.put("yyyy/MM/dd", "^\\d{4}/\\d{1,2}/\\d{1,2}$");
        FORMAT.put("yyyy/MM/dd HH", "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}");
        FORMAT.put("yyyy/MM/dd HH:mm", "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}$");
        FORMAT.put("yyyy/MM/dd HH:mm:ss", "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$");
    }

    /**
     * 格式化日期
     *
     * @param dateStr String 字符串日期
     * @param format String 格式
     * @return Date 日期
     */
    protected static Date parseDate(String dateStr, String format) {
        Date date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            // 严格模式
            dateFormat.setLenient(false);
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            log.info("转换日期失败, date={}, format={}", dateStr, format, e);
            throw new BizException(BASE_VALID_PARAM, e.getMessage());
        }
        return date;
    }

    @Override
    protected Map<String, String> getFormat() {
        return FORMAT;
    }

    @Override
    public Date convert(String dataStr) {
        return super.convert(dataStr, (format) -> parseDate(dataStr, format));
    }
}
