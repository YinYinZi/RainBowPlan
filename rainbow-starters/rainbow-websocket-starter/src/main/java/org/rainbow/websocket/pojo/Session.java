package org.rainbow.websocket.pojo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * 会话
 *
 * @author K
 * @date 2021/2/23  17:21
 */
public class Session {
    private final Channel channel;

    Session(Channel channel) {
        this.channel = channel;
    }

    public ChannelFuture sendText(String message) {
        return channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    public ChannelFuture sendText(ByteBuf byteBuf) {
        return channel.writeAndFlush(new TextWebSocketFrame(byteBuf));
    }

    public ChannelFuture sendText(ByteBuffer byteBuffer) {
        ByteBuf buffer = channel.alloc().buffer(byteBuffer.remaining());
        buffer.writeBytes(byteBuffer);
        return channel().writeAndFlush(new TextWebSocketFrame(buffer));
    }

    public ChannelFuture sendText(TextWebSocketFrame textWebSocketFrame) {
        return channel.writeAndFlush(textWebSocketFrame);
    }

    public ChannelFuture sendBinary(byte[] bytes) {
        ByteBuf buffer = channel.alloc().buffer(bytes.length);
        return channel.writeAndFlush(new BinaryWebSocketFrame(buffer.writeBytes(bytes)));
    }

    public ChannelFuture sendBinary(ByteBuf byteBuf) {
        return channel.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }

    public ChannelFuture sendBinary(ByteBuffer byteBuffer) {
        ByteBuf buffer = channel.alloc().buffer(byteBuffer.remaining());
        buffer.writeBytes(byteBuffer);
        return channel.writeAndFlush(new BinaryWebSocketFrame(buffer));
    }

    public ChannelFuture sendBinary(BinaryWebSocketFrame binaryWebSocketFrame) {
        return channel.writeAndFlush(binaryWebSocketFrame);
    }

    public void setSubProtocols(String subProtocols) {
        setAttribute("subprotocols", subProtocols);
    }

    public <T> void setAttribute(String name, T value) {
        AttributeKey<T> sessionIdKey = AttributeKey.valueOf(name);
        channel.attr(sessionIdKey).set(value);
    }

    public <T> T getAttribute(String name) {
        AttributeKey<T> sessionIdKey = AttributeKey.valueOf(name);
        return channel.attr(sessionIdKey).get();
    }

    public Channel channel() {
        return channel;
    }

    public ChannelId id() {
        return channel.id();
    }

    public ChannelConfig config() {
        return channel().config();
    }

    public boolean isOpen() {
        return channel().isOpen();
    }

    public boolean isRegistered() {
        return channel.isRegistered();
    }

    public boolean isActive() {
        return channel.isActive();
    }

    public ChannelMetadata metadata() {
        return channel().metadata();
    }

    public SocketAddress localAddress() {
        return channel().localAddress();
    }

    public SocketAddress remoteAddress() {
        return channel().remoteAddress();
    }

    public ChannelFuture closeFuture() {
        return channel().closeFuture();
    }

    public boolean isWritable() {
        return channel.isWritable();
    }

    public long bytesBeforeUnWritable() {
        return channel.bytesBeforeUnwritable();
    }

    public long bytesBeforeWritable() {
        return channel.bytesBeforeWritable();
    }

    public Channel.Unsafe unsafe() {
        return channel().unsafe();
    }

    public ChannelPipeline pipeline() {
        return channel().pipeline();
    }

    public ByteBufAllocator alloc() {
        return channel.alloc();
    }

    public Channel read() {
        return channel.read();
    }

    public Channel flush() {
        return channel.flush();
    }

    public ChannelFuture close() {
        return channel.close();
    }

    public ChannelFuture close(ChannelPromise promise) {
        return channel.close(promise);
    }
}
