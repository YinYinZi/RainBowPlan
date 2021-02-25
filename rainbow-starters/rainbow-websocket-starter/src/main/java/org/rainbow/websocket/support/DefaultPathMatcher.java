package org.rainbow.websocket.support;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * 默认的路径匹配实现
 *
 * @author K
 * @date 2021/2/25  17:47
 */
public class DefaultPathMatcher implements WsPathMatcher {

    private String pattern;

    public DefaultPathMatcher(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matchAndExtract(QueryStringDecoder decoder, Channel channel) {
        if (!pattern.equals(decoder.path())) {
            return false;
        }
        return true;
    }
}
