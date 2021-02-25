package org.rainbow.websocket.pojo;

import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.rainbow.websocket.standard.ServerEndpointConfig;
import org.rainbow.websocket.support.DefaultPathMatcher;
import org.rainbow.websocket.support.WsPathMatcher;

import java.util.*;

/**
 * @author K
 * @date 2021/2/24  13:45
 */
public class PojoEndpointServer {

    private static final AttributeKey<Object> POJO_KEY = AttributeKey.valueOf("WEBSOCKET_IMPLEMENT");

    public static final AttributeKey<Session> SESSION_KEY = AttributeKey.valueOf("WEBSOCKET_SESSION");

    private static final AttributeKey<String> PATH_KEY = AttributeKey.valueOf("WEBSOCKET_PATH");

    public static final AttributeKey<Map<String, String>> URI_TEMPLATE = AttributeKey.valueOf("WEBSOCKET_URI_TEMPLATE");

    public static final AttributeKey<Map<String, List<String>>> REQUEST_PARAM = AttributeKey.valueOf("WEBSOCKET_REQUEST_PARAM");

    private final Map<String, PojoMethodMapping> pathMethodMappingMap = new HashMap<>();

    private final ServerEndpointConfig config;

    private Set<WsPathMatcher> pathMatchers = new HashSet<>();

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PojoEndpointServer.class);

    public PojoEndpointServer(PojoMethodMapping methodMapping, ServerEndpointConfig config, String path) {

        this.config = config;
    }


    public void addPathPojoMethodMapping(String path, PojoMethodMapping pojoMethodMapping) {
        pathMethodMappingMap.put(path, pojoMethodMapping);

        pathMatchers.add(new DefaultPathMatcher(path));
    }
}
