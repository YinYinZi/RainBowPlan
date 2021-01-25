package org.rainbow.core.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.rainbow.core.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * 解决入参为LocalDateTime类型
 *
 * @author K
 * @date 2021/1/25  15:07
 */
public class String2LocalDateTimeConverter extends BaseDateConverter<LocalDateTime> implements Converter<String, LocalDateTime> {
    protected static final Map<String, String> FORMAT = new LinkedHashMap<>(2);

    static {
        FORMAT.put(DEFAULT_DATE_TIME_FORMAT, "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$");
        FORMAT.put("yyyy/MM/dd HH:mm:ss", "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$");
    }

    @Override
    protected Map<String, String> getFormat() {
        return FORMAT;
    }

    @Override
    public LocalDateTime convert(String dateStr) {
        return super.convert(dateStr, format -> LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(format)));
    }
}
