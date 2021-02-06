package org.rainbow.core.converter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * 自定义LocalDateTime反序列化实现
 *
 * @author K
 * @date 2021/2/5  9:58
 */
public class RainbowLocalDateTimeDeserializer extends JSR310DateTimeDeserializerBase<LocalDateTime> {
    public static final RainbowLocalDateTimeDeserializer INSTANCE = new RainbowLocalDateTimeDeserializer();
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DEFAULT_FORMATTER;
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT_DTF;
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT_EN_DTF;
    private static final DateTimeFormatter SLASH_DATE_FORMAT_DTF;
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT_DTF;
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT_EN_DTF;
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT_DTF;


    private RainbowLocalDateTimeDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public RainbowLocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class, formatter);
    }

    protected RainbowLocalDateTimeDeserializer(RainbowLocalDateTimeDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<LocalDateTime> withDateFormat(DateTimeFormatter formatter) {
        return new LocalDateTimeDeserializer(formatter);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<LocalDateTime> withLeniency(Boolean leniency) {
        return new RainbowLocalDateTimeDeserializer(this, leniency);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<LocalDateTime> withShape(JsonFormat.Shape shape) {
        return this;
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        if (parser.hasTokenId(6)) {
            String string = parser.getText().trim();
            if (string.length() == 0) {
                return null;
            } else {
                try {
                    if (this._formatter == null) {
                        return this.convert(string);
                    } else if (this._formatter == DEFAULT_FORMATTER) {
                        if (string.length() > 10 && string.charAt(10) == 'T') {
                            return string.endsWith("Z") ? LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC) : LocalDateTime.parse(string, DEFAULT_FORMATTER);
                        } else {
                            return this.convert(string);
                        }
                    } else {
                        return LocalDateTime.parse(string, this._formatter);
                    }
                } catch (DateTimeException var12) {
                    return (LocalDateTime)this._handleDateTimeException(context, var12, string);
                }
            }
        } else {
            if (parser.isExpectedStartArrayToken()) {
                JsonToken t = parser.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    return null;
                }

                LocalDateTime result;
                if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    result = this.deserialize(parser, context);
                    if (parser.nextToken() != JsonToken.END_ARRAY) {
                        this.handleMissingEndArrayForSingle(parser, context);
                    }

                    return result;
                }

                if (t == JsonToken.VALUE_NUMBER_INT) {
                    int year = parser.getIntValue();
                    int month = parser.nextIntValue(-1);
                    int day = parser.nextIntValue(-1);
                    int hour = parser.nextIntValue(-1);
                    int minute = parser.nextIntValue(-1);
                    t = parser.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        result = LocalDateTime.of(year, month, day, hour, minute);
                    } else {
                        int second = parser.getIntValue();
                        t = parser.nextToken();
                        if (t == JsonToken.END_ARRAY) {
                            result = LocalDateTime.of(year, month, day, hour, minute, second);
                        } else {
                            int partialSecond = parser.getIntValue();
                            if (partialSecond < 1000 && !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                                partialSecond *= 1000000;
                            }

                            if (parser.nextToken() != JsonToken.END_ARRAY) {
                                throw context.wrongTokenException(parser, this.handledType(), JsonToken.END_ARRAY, "Expected array to end");
                            }

                            result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
                        }
                    }

                    return result;
                }

                context.reportInputMismatch(this.handledType(), "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT", new Object[]{t});
            }

            if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return Instant.ofEpochMilli(parser.getLongValue()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
            } else {
                return parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT) ? (LocalDateTime)parser.getEmbeddedObject() : (LocalDateTime)this._handleUnexpectedToken(context, parser, "当前参数需要数组、字符串、时间戳。", new Object[0]);
            }
        }
    }

    private LocalDateTime convert(String source) {
        if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return LocalDateTime.of(LocalDate.parse(source, DEFAULT_DATE_FORMAT_DTF), LocalTime.MIN);
        } else if (source.matches("^\\d{4}年\\d{1,2}月\\d{1,2}日$")) {
            return LocalDateTime.of(LocalDate.parse(source, DEFAULT_DATE_FORMAT_EN_DTF), LocalTime.MIN);
        } else if (source.matches("^\\d{4}/\\d{1,2}/\\d{1,2}$")) {
            return LocalDateTime.of(LocalDate.parse(source, SLASH_DATE_FORMAT_DTF), LocalTime.MIN);
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return LocalDateTime.parse(source, DEFAULT_DATE_TIME_FORMAT_DTF);
        } else if (source.matches("^\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒$")) {
            return LocalDateTime.parse(source, DEFAULT_DATE_TIME_FORMAT_EN_DTF);
        } else {
            return source.matches("^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$") ? LocalDateTime.parse(source, SLASH_DATE_TIME_FORMAT_DTF) : null;
        }
    }

    static {
        DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DEFAULT_DATE_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DEFAULT_DATE_FORMAT_EN_DTF = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        SLASH_DATE_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DEFAULT_DATE_TIME_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DEFAULT_DATE_TIME_FORMAT_EN_DTF = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分ss秒");
        SLASH_DATE_TIME_FORMAT_DTF = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    }
}
