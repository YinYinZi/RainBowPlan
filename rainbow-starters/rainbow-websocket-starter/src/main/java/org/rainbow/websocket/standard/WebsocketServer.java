package org.rainbow.websocket.standard;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.rainbow.websocket.pojo.PojoEndpointServer;

/**
 * websocket 服务
 *
 * @author K
 * @date 2021/2/25  15:23
 */
public class WebsocketServer {


    private final PojoEndpointServer pojoEndpointServer;

    /**
     * 服务端口配置
     */
    private final ServerEndpointConfig config;

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(WebsocketServer.class);

    public WebsocketServer(PojoEndpointServer webSocketHandler, ServerEndpointConfig config) {
        this.pojoEndpointServer = webSocketHandler;
        this.config = config;
    }
}
