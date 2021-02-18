package org.rainbow.dingtalk.sdk;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.rainbow.dingtalk.sdk.message.DingTalkMessage;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author K
 * @date 2021/2/18  14:45
 */
@Slf4j
public class DingTalkSender {
    private final String url;
    private String secret;
    private final HttpRequest request;
    private final Mac mac;

    public DingTalkSender(String url) throws NoSuchAlgorithmException {
        try {
            this.url = url;
            this.request = HttpUtil.createPost(url);
            this.mac = Mac.getInstance("HmacSHA256");
        } catch (Throwable var3) {
            throw var3;
        }
    }

    public DingTalkResponse sendMessage(DingTalkMessage message) throws IOException {
        try {
            return StrUtil.isEmpty(this.secret) ? this.sendNormalMessage(message) : this.sendSecretMessage(message);
        } catch (Throwable var3) {
            throw var3;
        }
    }

    public DingTalkResponse sendNormalMessage(DingTalkMessage message) {
        try {
            String body = this.request.body(message.generate()).execute().body();
            return new DingTalkResponse(body);
        } catch (IOException e) {
            log.error("发送钉钉消息失败：{}", e.getMessage());
        }
        return null;
    }

    public DingTalkResponse sendSecretMessage(DingTalkMessage message) {
        try {
            String body = this.request.setUrl(this.secret(System.currentTimeMillis())).body(message.generate()).execute().body();
            return new DingTalkResponse(body);
        } catch (Throwable var3) {
            log.error("发送钉钉消息失败：{}", var3.getMessage());
        }
        return null;
    }

    public DingTalkSender setSecret(String secret) throws InvalidKeyException {
        try {
            if (StrUtil.isNotEmpty(secret)) {
                this.secret = secret;
                this.mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            }

            return this;
        } catch (Throwable var3) {
            throw var3;
        }
    }

    public String secret(long timestamp) throws UnsupportedEncodingException {
        try {
            return this.url + "&timestamp=" + timestamp + "&sign=" + URLEncoder.encode(Base64.encode(this.mac.doFinal((timestamp + "\n" + this.secret).getBytes(StandardCharsets.UTF_8))), "UTF-8");
        } catch (Throwable var4) {
            throw var4;
        }
    }
}
