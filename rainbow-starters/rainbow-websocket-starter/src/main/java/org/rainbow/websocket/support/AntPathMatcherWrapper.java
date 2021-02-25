package org.rainbow.websocket.support;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.springframework.util.AntPathMatcher;

import java.util.LinkedHashMap;

import static org.rainbow.websocket.pojo.PojoEndpointServer.URI_TEMPLATE;

/**
 * @author K
 * @date 2021/2/24  11:50
 */
public class AntPathMatcherWrapper extends AntPathMatcher implements WsPathMatcher {

    private String pattern;

    public AntPathMatcherWrapper(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matchAndExtract(QueryStringDecoder decoder, Channel channel) {
        LinkedHashMap<String, String> variables = new LinkedHashMap<>();
        boolean result = doMatch(pattern, decoder.path(), true, variables);
        if (result) {
            channel.attr(URI_TEMPLATE).set(variables);
            return true;
        }
        return false;
    }
}
