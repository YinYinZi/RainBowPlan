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
    private final String host;

    /**
     * websocket port
     */
    private final int port;

    /**
     * bossEventLoopGroup的线程数
     */
    private final int bossLoopGroupThreads;

    /**
     * workerEventLoopGroup的线程数
     */
    private final int workerLoopGroupThreads;

    /**
     * 是否添加WebSocketServerCompressionHandler到pipeline
     */
    private final boolean useCompressionHandler;

    /**
     * 连接超时毫秒数
     */
    private final int connectTimeoutMillis;

    /**
     * 可连接队列的大小
     */
    private final int soBackLog;

    /**
     * 一个Loop写操作执行的最大次数，默认值为16,也就是说，对于大数据量的写操作至多进行16次，
     * 如果16次仍没有全部写完数据，此时会提交一个新的写任务给EventLoop，
     * 任务将在下次调度继续执行,其他的写请求不会因为单个大数据量写请求而耽误
     */
    private final int writeSpinCount;

    /**
     * AbstractChannelHandlerContext.write()时会将消息方法ChannelOutBoundBuffer中,
     * 然后会调用buffer.incrementPendingOutboundBytes(size)来增加缓冲区大小
     * 为了避免缓冲区增长过快, 当缓冲区数据大小超过High_Water_Mark时会把Channel标记为不可写
     * 在flush过程中如果writeBufferSize降低到Low_Water_Mark时, 会重新将Channel标记为可写
     */
    private final int writeBufferHighWaterMark;
    private final int writeBufferLowWaterMark;

    /**
     * 接受缓冲区的大小
     */
    private final int soRcvBuf;

    /**
     * 发送缓冲区的大小
     */
    private final int soSndBuf;


    /**
     * 是否开启Nagle算法
     */
    private final boolean tcpNoDelay;

    /**
     * 心跳检测开关
     */
    private final boolean soKeepAlive;

    /**
     * 设置延时关闭的时间
     */
    private final int soLinger;

    /**
     * 关闭连接时，允许半关
     */
    private final boolean allowHalfClosure;

    /**
     * 读超时. 即当在指定的时间间隔内没有从 Channel 读取到数据时, 会触发一个 READER_IDLE 的 IdleStateEvent 事件
     */
    private final int readerIdleTimeSeconds;

    /**
     * 写超时. 即当在指定的时间间隔内没有数据写入到 Channel 时, 会触发一个 WRITER_IDLE 的 IdleStateEvent 事件
     */
    private final int writerIdleTimeSeconds;

    /**
     * 读/写超时. 即当在指定的时间间隔内没有读或写操作时, 会触发一个 ALL_IDLE 的 IdleStateEvent 事件
     */
    private final int allIdleTimeSeconds;

    /**
     * 最大允许帧载荷长度
     */
    private final int maxFramePayloadLength;

    /**
     * 是否使用另一个线程池来执行耗时的同步业务逻辑
     */
    private final boolean useEventExecutorGroup;

    /**
     * eventExecutorGroup的线程数
     */
    private final int eventExecutorGroupThreads;


    private final String keyPassword;

    private final String keyStore;

    private final String keyStorePassword;

    private final String keyStoreType;

    private final String trustStore;

    private final String trustStorePassword;

    private final String trustStoreType;

    private final String[] corsOrigins;
    private final Boolean corsAllowCredentials;

    private static Integer randomPort;

    public ServerEndpointConfig(String host, int port, int bossLoopGroupThreads,
                                int workerLoopGroupThreads, boolean useCompressionHandler,
                                int connectTimeoutMillis, int soBacklog, int writeSpinCount,
                                int writeBufferHighWaterMark, int writeBufferLowWaterMark,
                                int soRcvBuf, int soSndBuf, boolean tcpNoDelay, boolean soKeepalive,
                                int soLinger, boolean allowHalfClosure, int readerIdleTimeSeconds,
                                int writerIdleTimeSeconds, int allIdleTimeSeconds, int maxFramePayloadLength,
                                boolean useEventExecutorGroup, int eventExecutorGroupThreads, String keyPassword,
                                String keyStore, String keyStorePassword, String keyStoreType, String trustStore,
                                String trustStorePassword, String trustStoreType, String[] corsOrigins,
                                Boolean corsAllowCredentials) {
        if (StringUtils.isEmpty(host) || "0.0.0.0".equals(host) || "0.0.0.0/0.0.0.0".equals(host)) {
            this.host = "0.0.0.0";
        } else {
            this.host = host;
        }
        this.port = getAvailablePort(port);
        this.bossLoopGroupThreads = bossLoopGroupThreads;
        this.workerLoopGroupThreads = workerLoopGroupThreads;
        this.useCompressionHandler = useCompressionHandler;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.soBackLog = soBacklog;
        this.writeSpinCount = writeSpinCount;
        this.writeBufferHighWaterMark = writeBufferHighWaterMark;
        this.writeBufferLowWaterMark = writeBufferLowWaterMark;
        this.soRcvBuf = soRcvBuf;
        this.soSndBuf = soSndBuf;
        this.tcpNoDelay = tcpNoDelay;
        this.soKeepAlive = soKeepalive;
        this.soLinger = soLinger;
        this.allowHalfClosure = allowHalfClosure;
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
        this.allIdleTimeSeconds = allIdleTimeSeconds;
        this.maxFramePayloadLength = maxFramePayloadLength;
        this.useEventExecutorGroup = useEventExecutorGroup;
        this.eventExecutorGroupThreads = eventExecutorGroupThreads;

        this.keyPassword = keyPassword;
        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        this.keyStoreType = keyStoreType;
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
        this.trustStoreType = trustStoreType;

        this.corsOrigins = corsOrigins;
        this.corsAllowCredentials = corsAllowCredentials;
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