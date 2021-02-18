package org.rainbow.dingtalk.sdk;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.rainbow.dingtalk.sdk.message.DingTalkActionCardMessage;

import java.util.List;
import java.util.Set;

/**
 * @author K
 * @date 2021/2/18  14:12
 */
@Data
@Slf4j
@Accessors(chain = true)
public class DingTalkParams {
    @JsonProperty("msgtype")
    private String type;
    private DingTalkParams.At at;
    private DingTalkParams.ActionCard actionCard;
    private DingTalkParams.Link link;
    private DingTalkParams.Markdown markdown;
    private DingTalkParams.Text text;

    @Override
    public String toString() {
        try {
            return (new ObjectMapper()).writeValueAsString(this);
        } catch (Throwable var2) {
            log.error("消息序列化失败：{}", var2.getMessage());
        }
        return "";
    }

    @Data
    @NoArgsConstructor
    public static class At {
        @JsonProperty("isAtAll")
        private boolean atAll;
        private Set<String> atMobiles;
    }

    @Data
    @NoArgsConstructor
    public static class ActionCard {
        private String title;
        private String text;
        private String btnOrientation;
        private String singleTitle;
        @JsonProperty("singleURL")
        private String singleUrl;
        @JsonProperty("btns")
        private List<DingTalkActionCardMessage.Button> buttons;
    }

    @Data
    @NoArgsConstructor
    public static class Link {
        private String text;
        private String title;
        private String picUrl;
        private String messageUrl;
    }

    @Data
    @NoArgsConstructor
    public static class Markdown {
        private String title;
        private String text;
    }

    @Data
    @NoArgsConstructor
    public static class Text {
        private String content;
    }
}
