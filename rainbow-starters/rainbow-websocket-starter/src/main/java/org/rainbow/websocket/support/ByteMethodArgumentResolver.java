package org.rainbow.websocket.support;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.rainbow.websocket.annotation.OnBinary;
import org.springframework.core.MethodParameter;

/**
 * byte数组方法参数解析器
 *
 * @author K
 * @date 2021/2/25  10:05
 */
public class ByteMethodArgumentResolver implements MethodArgumentResolver {

    /**
     * 判断给定的方法是否标注OnBinary注解并且方法参数的参数类型是否与byte[]类型一样或是其子类
     *
     * @param methodParameter 方法参数
     * @return true / false
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getMethod().isAnnotationPresent(OnBinary.class) &&
                byte[].class.isAssignableFrom(methodParameter.getParameterType());
    }

    /**
     * 将给定的方法参数解析为byte[]数组
     *
     * @param parameter 方法参数
     * @param channel   通道
     * @param object    object对象
     * @return byte[]数组对象
     * @throws Exception 异常
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        // 将对象强转为二进制数据Web套接字框架
        BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) object;
        // 获取内容缓冲区
        ByteBuf content = binaryWebSocketFrame.content();
        // 创建与内容长度相同的byte数组
        byte[] bytes = new byte[content.readableBytes()];
        // 将内容缓冲区数据写入byte数组中
        content.readBytes(bytes);
        return bytes;
    }
}
