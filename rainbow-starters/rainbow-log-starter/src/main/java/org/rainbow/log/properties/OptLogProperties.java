package org.rainbow.log.properties;

import com.google.common.base.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 操作日志配置属性
 *
 * @author K
 * @date 2021/2/9  10:12
 */
@ConfigurationProperties(
        prefix = "rainbow.log"
)
public class OptLogProperties {
    public static final String PREFIX = "rainbow.log";
    private Boolean enable = true;
    private OptLogType type;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public OptLogType getOptLogType() {
        return type;
    }

    public void setOptLogType(OptLogType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OptLogProperties that = (OptLogProperties) o;
        return Objects.equal(enable, that.enable) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(enable, type);
    }

    @Override
    public String toString() {
        return "OptLogProperties{" +
                "enable=" + enable +
                ", type=" + type +
                '}';
    }

    public OptLogProperties() {
        this.type = OptLogType.DB;
    }
}
