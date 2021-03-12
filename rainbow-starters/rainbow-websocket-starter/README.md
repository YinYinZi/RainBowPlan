### rainbow-websocket-starter 项目解析

#### 程序入口类：ServerEndpointExporter
该类储存着InetSocketAddress和WebsocketServer映射

`ServerEndpointExporter` 继承了 `ApplicationObjectSupport` 实现了 `SmartInitializingSingleton` 和 `BeanFactoryAware`

❗ 继承抽象类 `ApplicationObjectSupport` 会在Spring初始化的时候通过该抽象类的setApplicationContext(ApplicationContext ctx)
将ApplicationContext进行注入 并提供了getApplicationContext()方便获取ApplicationContext

❗ 实现了接口 `SmartInitializingSingleton` 会在所有bean都初始化之后 容器会回调该接口的方法 `afterSingletonInstantiated()`

❗ 实现了接口 `BeanFactoryAware` 会将 `BeanFactory` 对象注入进来

具体执行过程：
1. afterSingletonInstantiated()方法中调用registerServerEndpoints()注册所有websocket服务端点
2. registerServerEndpoints()中将会在查找所有标注了注解@ServerEndpoint的Bean 根据beanName查找该bean的Class
3. 接着会调用registerServerEndpoint(endpointClass)
4. 在registerServerEndpoint()中构建pojoMethodMapping new PojoMethodMapping(endpointClass, ctx, beanFactory)
5. 将@ServerEndpoint注解上的所有属性、pojoMethodMapping、path传入PojoEndpointServer构造器
6. 传入PojoEndpointServer初始化WebsocketServer
7. 保存InetSocketAddress和WebSocketServer映射
8. 循环逐一调用init()初始化WebSocketServer(方法中调用了WebSocketServer的init())

#### 记录PojoEndpointServer和ServerEndpointConfig的对象 WebsocketServer

```
public void init() {
    EventExecutorGroup eventExecutorGroup = null;
    // 判断是否是用来ssl加密
    final SslContext sslCtx;
    if (StrUtil.isNotBlank(this.config.getKeyStore())) {
        sslCtx = SslUtils.createSslContext(config.getKeyPassword(), config.getKeyStore,
                config.getKeyStoreType(), config.getKeyStorePassword(),
                config.getTrustStoreType(), config.getTrustStorePassword());
    } else {
        sslCtx = null;
    }
    String[] corsOrigins = config.getCorsOrigins();
    Boolean corsAllowCredentials = config.getCorsAllowCredentials();
    final CorsConfig corsConfig = createCors(corsOrigins, corsAllowCredentials);

    if (config.isUseEventExecutor) {
        eventExecutorGroup = new DefaultEventExecutorGroup(config.getEvecutorGroupThreads() == 0 ? 16 : config.getEventExecutorGroupThreads());
    }
    
    NioEventLoopGroup boos = new NioEventLoopGroup(config.getBossLoopGroupThreads());
    NioEventLoopGroup worker = new NioEvnetLoopGroup(config.getWorkerLoopGroupThreads());
    ServerBootStrap bootstrap = new ServerBootstrap();
    EventExecutorGroup finalExecutorGroup = eventExecutorGroup;
    bootstrap.group(boss, worker)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeoutMillis())
            .option(ChannelOption.SO_BACKLOG, config.getSoBlackLog())
            .childOption(ChannelOption.WRITE_SPIN_COUNT, config.getWriteSpinCount())
            .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(config.getWriteBufferLowWaterMark(), config.getWriteBufferHighWaterMark()))
            .childOption(ChannelOption.TCP_NODELAY, config.isTcpNoDelay())
            .childOption(ChannelOption.SO_KEEPALIVE, config.isSoKeepAlive())
            .childOption(ChannelOption.SO_LINGER, config.getSoLinger())
            .childOption(ChannelOption.ALLOW_HALF_CLOSURE, config.isAllowHalfClosure())
            .handler(new LoggingHandler(LogLevel.DEBUG))
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    if (sslCtx != null) {
                        pipeline.addLast(sslCtx.newHandler(ch.alloc()));
                    }
                    pipeline.addLast(new HttpServerCodec());
                    pipeline.addLast(new HttpObjectAggregator(65536));
                    if (corsConfig != null) {
                        pipeline.addLast(new CorsHandler(corsConfig));
                    }
                    pipeline.addLast(new HttpServerHandler(pojoEndpointServer, config, finalExecutorGroup, corsConfig != null));
                }
            });
    
    if (config.getSoRcvBuf() != -1) {
        bootstrap.childOption(ChannelOption.SO_RCVBUF, config.getSoRcvBuf());
    }
    if (config.getSoSndBuf() != -1) {
        bootstrap.childOption(ChannelOption.SO_SNDBUF, config.getSoSndBuf());
    }

    ChannelFuture channelFuture = null;
    if ("0.0.0.0".equals(config.getHost())) {
        channelFuture = bootstrap.bind(config.getPort());
    } else {
        try {
            bootstrap.bind(new InetSocketAddress(InetAddress.getByName(config.getHost(), config.getPort())));
        } catch(UnknownHostException e) {
            channelFuture = bootstrap.bind(config.getHost(), config.getPort);
            e.printStackTrace();
        }
    }

    channelFuture.addListener(future -> {
        if (!future.isSuccess()) {
            future.cause.printStackTrace();
        }
    });

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        boss.shutdownGracefully().syncUninterruptibly();
        worker.shutdownGracefully().syncUninterruptibly();
    }));
}
```
注意这里的HttpServerHandler.

#### 记录路径和PojoMethodMapping的映射对象 PojoEndpointServer

该类存储了路径和PojoMethodMapping的映射、ServerEndpoint的配置属性。
并提供了addPathPojoMethodMapping(path, pojoMethodMapping)

```
public void addPathPojoMethodMapping(String path, PojoMethodMapping pojoMethodMapping) {
    pathMethodMappingMap.put(path, pojoMethodMapping);
    for (MethodArgumentResolver onOpenArgResolver : pojoMethodMapping.getOpenArgResolvers) {
        if (onOpenArgResolver instanceof PathVariableMethodArgumentResolver 
            || onOpenArgResolver instanceof PathVariableMapMethodArgumentResolver) {
            pathMatchers.add(new AntPathMatcherWrapper(path));
            return;
        }
    }
    pathMatchers.add(new DefaultPathMatcher(path));
}
```


#### 记录websocket具体方法和方法参数解析器的对象 PojoMethodMapping

在PojoMethodMapping的构造方法中, 通过传入的EndpointServer.class调用getDeclaredMethods()查找到定义在类上的所有方法.
循环判断所有方法是否有对应的Annotation.然后逐级往上
```
public PojoMethodMapping(Class<?> clazz, ApplicationContext ctx, AbstractBeanFactory beanFactory) {
    .
    .
    while (!clazz.equals(Object.class)) {
        for (Method method : clazzMethods) {
            .
            .
        }
        clazz = clazz.getSuperClass();
    }
    .
    .
    // 逐个获取每个方法的方法参数
    getParameter(method);
    // 逐个获取每个方法的方法参数的参数解析器
    getResolvers(parameters);
}
```

然后查找方法的方法参数
```
private static MethodParameter[] getParameters(Method m) {
    if (m == null) {
        return new MethodParameter[0];
    }
    // 初始化结果数组
    int parameterCount = m.getParameterCount();
    MethodParameter[] result = new MethodParameter[parameterCount];
    // 循环获取方法参数
    for (int i = 0; i < parameterCount; i++) {
        MethodParameter methodParameter = new MethodParameter(m, i);
        methodParameter.initParameterNameDiscovery(DISCOVERER);
        result[i] = methodParameter;
    }
    return result;
}
```

然后查找每个方法的每个参数的参数解析器
```
private MethodArgumentResolver[] getResolvers(MethodParameter[] parameters) {
    MethodArgumentResolver[] methodArgumentResolvers =  new MethodArgumentResolver[parameters.length];
}

private List<MethodArgumentResolver> getDefaultResolvers() {
    List<MethodArgumentResolver> resolvers = new ArrayList<>();
    // Session参数类型解析器
    resolvers.add(new SessionMethodArgumentResolver());
    // HttpHeaders参数类型解析器
    resolvers.add(new HttpHeadersMethodArgumentResolver());
    // TextWebSocketFrame参数类型解决器
    resolvers.add(new TextMethodArgumentResolver());
    // Throwable参数类型解析器
    resolvers.add(new ThrowableMethodArgumentResolver());
    // Byte数组参数类型解析器
    resolvers.add(new ByteMethodArgumentResolver());
    // RequestParamMap参数类型解析器
    resolvers.add(new RequestParamMapMethodArgumentResolver());
    // RequestParam参数类型解析器
    resolvers.add(new RequestParamMethodArgumentResolver());
    // PathVariableMap参数类型解析器
    resolvers.add(new PathVariableMap);
}
```

####  FullHttpRequest类型消息处理器 HttpServerHandler

```
/**
 * 当异常发生时调用
 */ 
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    pojoEndpointServer.doError(ctx.channel(), cause);
}

/**
 * 通道关闭时调用
 */
public void channelInactive(ChannelHandlerContext ctx) {
    pojoEndpointServer.doOnClose(ctx.channel());
    super.channelInactive(ctx);
}

/**
 * 当收到FullHttpRequest消息时调用
 */
protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
    handlerHttpRequest(ctx, meg);
    .
    .
}

private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
    .
    .
    // ChannelPipeline类是ChannelHandler实例对象的链表，用于处理或截获通道的接收和发送数据
    ChannelPipeline pipeline = ctx.pipeline();
    .
    pipeline.addLast(new WebSocketServerHandler(pojoEndpointServer));
    .
}
```

#### WebSocketFrame类型消息处理器

```
protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
    handleWebSocketFrame(ctx, msg);
}

private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
    if (frame instanceof TextWebSocketFrame) {
        pojoEndpointServer.doOnMessage(ctx.channel(), frame);
        return;
    }
    if (frame instanceof PingWebSocketFrame) {
        ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
        return;
    }
    if (frame instanceof CloseWebSocketFrame) {
        ctx.writeAndFlush(frame.retainedDuplicate()).addListener(ChannelFutureListener.CLOSE);
        return;
    }
    if (frame instanceof BinaryWebSocketFrame) {
        pojoEndpointServer.doOnBinary(ctx.channel(), frame);
        return;
    }
    if (frame instanceof PongWebSocketFrame) {
        return;
    }
}
```


#### 自定义Method Annotation
* @BeforeHandshake 当有新连接进入的时候 会对该方法进行回调 注入的参数的类型：Session、HttpHeaders
* @OnOpen 当有新的WebSocket连接完成时 会对该方法进行回调 注入的参数的类型：Session、HttpHeaders
* @OnClose 当有WebSocket连接关闭时 会对该方法进行回调 注入的参数的类型：Session
* @OnError 当有WebSocket抛出异常时 会对该方法进行回调 注入的参数的类型：Session、Throwable
* @OnMessage 当接收到字符串消息时 对该方法进行回调 注入的参数的类型：Session、String
* @OnBinary 当接收到二进制消息时 对该方法进行回调 注入的参数的类型：Session、byte[]
* @OnEvent 当接收到Netty的事件时 对该方法进行回调 注入的参数的类型：Session、Object