package org.rainbow.websocket.support;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.QueryStringDecoder;


/**
 * websocket路径匹配策略接口
 *
 * @author K
 * @date 2021/2/24  11:48
 */
public interface WsPathMatcher {

    /**
     * 获取路径
     *
     * @return 路径
     */
    String getPattern();

    /**
     * 匹配路径并提取数据
     *
     * @param decoder decoder将路径 分割成 path 和 key-value 参数对
     * @param channel 通道
     * @return true / false
     */
    boolean matchAndExtract(QueryStringDecoder decoder, Channel channel);
}
