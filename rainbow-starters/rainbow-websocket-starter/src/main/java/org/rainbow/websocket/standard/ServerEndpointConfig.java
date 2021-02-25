package org.rainbow.websocket.standard;

import io.netty.channel.local.LocalAddress;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.Socket;

/**
 * websocket 配置类
 *
 * @author K
 * @date 2021/2/24  14:02
 */
@Getter
public class ServerEndpointConfig {

    /**
     * websocket host
     */
    private final String HOST;

    /**
     * websocket port
     */
    private final int PORT;

    /**
     * bossEventLoopGroup的线程数
     */
    private final int BOSS_LOOP_GROUP_THREADS;

    /**
     * workerEventLoopGroup的线程数
     */
    private final int WORKER_LOOP_GROUP_THREADS;

    /**
     * 是否添加WebSocketServerCompressionHandler到pipeline
     */
    private final boolean USE_COMPRESSION_HANDLER;

    /**
     * 连接超时毫秒数
     */
    private final int CONNECT_TIMEOUT_MILLIS;

    /**
     * 可连接队列的大小
     */
    private final int SO_BACKLOG;

    /**
     * 一个Loop写操作执行的最大次数，默认值为16,也就是说，对于大数据量的写操作至多进行16次，
     * 如果16次仍没有全部写完数据，此时会提交一个新的写任务给EventLoop，
     * 任务将在下次调度继续执行,其他的写请求不会因为单个大数据量写请求而耽误
     */
    private final int WRITE_SPIN_COUNT;

    /**
     * AbstractChannelHandlerContext.write()时会将消息方法ChannelOutBoundBuffer中,
     * 然后会调用buffer.incrementPendingOutboundBytes(size)来增加缓冲区大小
     * 为了避免缓冲区增长过快, 当缓冲区数据大小超过High_Water_Mark时会把Channel标记为不可写
     * 在flush过程中如果writeBufferSize降低到Low_Water_Mark时, 会重新将Channel标记为可写
     */
    private final int WRITE_BUFFER_HIGH_WATER_MARK;
    private final int WRITE_BUFFER_LOW_WATER_MARK;

    /**
     * 接受缓冲区的大小
     */
    private final int SO_RCVBUF;

    /**
     * 发送缓冲区的大小
     */
    private final int SO_SNDBUF;


    /**
     * 是否开启Nagle算法
     */
    private final boolean TCP_NODELAY;

    /**
     * 心跳检测开关
     */
    private final boolean SO_KEEPALIVE;

    /**
     * 设置延时关闭的时间
     */
    private final int SO_LINGER;

    /**
     * 关闭连接时，允许半关
     */
    private final boolean ALLOW_HALF_CLOSURE;

    /**
     * 读超时. 即当在指定的时间间隔内没有从 Channel 读取到数据时, 会触发一个 READER_IDLE 的 IdleStateEvent 事件
     */
    private final int READER_IDLE_TIME_SECONDS;

    /**
     * 写超时. 即当在指定的时间间隔内没有数据写入到 Channel 时, 会触发一个 WRITER_IDLE 的 IdleStateEvent 事件
     */
    private final int WRITER_IDLE_TIME_SECONDS;

    /**
     * 读/写超时. 即当在指定的时间间隔内没有读或写操作时, 会触发一个 ALL_IDLE 的 IdleStateEvent 事件
     */
    private final int ALL_IDLE_TIME_SECONDS;

    /**
     * 最大允许帧载荷长度
     */
    private final int MAX_FRAME_PAYLOAD_LENGTH;

    /**
     * 是否使用另一个线程池来执行耗时的同步业务逻辑
     */
    private final boolean USE_EVENT_EXECUTOR_GROUP;

    /**
     * eventExecutorGroup的线程数
     */
    private final int EVENT_EXECUTOR_GROUP_THREADS;


    private final String KEY_PASSWORD;

    private final String KEY_STORE;

    private final String KEY_STORE_PASSWORD;

    private final String KEY_STORE_TYPE;

    private final String TRUST_STORE;

    private final String TRUST_STORE_PASSWORD;

    private final String TRUST_STORE_TYPE;

    private final String[] CORS_ORIGINS;
    private final Boolean CORS_ALLOW_CREDENTIALS;

    private static Integer randomPort;

    public ServerEndpointConfig(String host, int port, int bossLoopGroupThreads,
                                int workerLoopGroupThreads, boolean useCompressionHandler,
                                int connectTimeoutMillis, int soBacklog, int writeSpinCount,
                                int writeBufferHighWaterMark, int writeBufferLowWaterMark,
                                int soRcvbuf, int soSndbuf, boolean tcpNodelay, boolean soKeepalive,
                                int soLinger, boolean allowHalfClosure, int readerIdleTimeSeconds,
                                int writerIdleTimeSeconds, int allIdleTimeSeconds, int maxFramePayloadLength,
                                boolean useEventExecutorGroup, int eventExecutorGroupThreads, String keyPassword,
                                String keyStore, String keyStorePassword, String keyStoreType, String trustStore,
                                String trustStorePassword, String trustStoreType, String[] corsOrigins,
                                Boolean corsAllowCredentials) {
        if (StringUtils.isEmpty(host) || "0.0.0.0".equals(host) || "0.0.0.0/0.0.0.0".equals(host)) {
            this.HOST = "0.0.0.0";
        } else {
            this.HOST = host;
        }
        this.PORT = getAvailablePort(port);
        this.BOSS_LOOP_GROUP_THREADS = bossLoopGroupThreads;
        this.WORKER_LOOP_GROUP_THREADS = workerLoopGroupThreads;
        this.USE_COMPRESSION_HANDLER = useCompressionHandler;
        this.CONNECT_TIMEOUT_MILLIS = connectTimeoutMillis;
        this.SO_BACKLOG = soBacklog;
        this.WRITE_SPIN_COUNT = writeSpinCount;
        this.WRITE_BUFFER_HIGH_WATER_MARK = writeBufferHighWaterMark;
        this.WRITE_BUFFER_LOW_WATER_MARK = writeBufferLowWaterMark;
        this.SO_RCVBUF = soRcvbuf;
        this.SO_SNDBUF = soSndbuf;
        this.TCP_NODELAY = tcpNodelay;
        this.SO_KEEPALIVE = soKeepalive;
        this.SO_LINGER = soLinger;
        this.ALLOW_HALF_CLOSURE = allowHalfClosure;
        this.READER_IDLE_TIME_SECONDS = readerIdleTimeSeconds;
        this.WRITER_IDLE_TIME_SECONDS = writerIdleTimeSeconds;
        this.ALL_IDLE_TIME_SECONDS = allIdleTimeSeconds;
        this.MAX_FRAME_PAYLOAD_LENGTH = maxFramePayloadLength;
        this.USE_EVENT_EXECUTOR_GROUP = useEventExecutorGroup;
        this.EVENT_EXECUTOR_GROUP_THREADS = eventExecutorGroupThreads;

        this.KEY_PASSWORD = keyPassword;
        this.KEY_STORE = keyStore;
        this.KEY_STORE_PASSWORD = keyStorePassword;
        this.KEY_STORE_TYPE = keyStoreType;
        this.TRUST_STORE = trustStore;
        this.TRUST_STORE_PASSWORD = trustStorePassword;
        this.TRUST_STORE_TYPE = trustStoreType;

        this.CORS_ORIGINS = corsOrigins;
        this.CORS_ALLOW_CREDENTIALS = corsAllowCredentials;
    }

    private int getAvailablePort(int port) {
        if (port != 0) {
            return port;
        }
        if (randomPort != null && randomPort != 0) {
            return randomPort;
        }
        InetSocketAddress inetSocketAddress = new InetSocketAddress(0);
        Socket socket = new Socket();
        try {
            socket.bind(inetSocketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int localPort = socket.getLocalPort();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        randomPort = localPort;
        return localPort;
    }
}