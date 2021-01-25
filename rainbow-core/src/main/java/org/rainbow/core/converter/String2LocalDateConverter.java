package org.rainbow.core.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.rainbow.core.utils.DateUtils.DEFAULT_DATE_FORMAT;

/**
 * 解决入参为Date类型
 *
 * @author K
 * @date 2021/1/25  14:59
 */
public class String2LocalDateConverter extends BaseDateConverter<LocalDate> implements Converter<String, LocalDate> {

    protected static final Map<String, String> FORMAT = new LinkedHashMap<>(2);

    static {
        FORMAT.put(DEFAULT_DATE_FORMAT, "^\\d{4}-\\d{1,2}-\\d{1,2}$");
        FORMAT.put("yyyy/MM/dd", "^\\d{4}/\\d{1,2}/\\d{1,2}$");
    }

    @Override
    protected Map<String, String> getFormat() {
        return FORMAT;
    }

    @Override
    public LocalDate convert(String dateStr) {
        return super.convert(dateStr, (format) -> LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format)));
    }
}
