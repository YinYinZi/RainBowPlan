package org.rainbow.dingtalk.sdk;

import cn.hutool.core.convert.Convert;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.util.Map;

/**
 * @author K
 * @date 2021/2/18  14:42
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class DingTalkResponse {
    public static final Long SUCCESS_CODE = 0L;
    private Long code;
    private String message;
    private String response;
    private boolean success;

    public DingTalkResponse(String res) throws IOException {
        try {
            Map resMap = (Map)(new ObjectMapper()).readValue(res.getBytes(), Map.class);
            this.response = res;
            this.code = Convert.toLong(resMap.get("errcode"));
            this.message = Convert.toStr(resMap.get("errmsg"));
            this.success = SUCCESS_CODE.equals(this.code);
        } catch (Throwable var3) {
            throw var3;
        }
    }
}
