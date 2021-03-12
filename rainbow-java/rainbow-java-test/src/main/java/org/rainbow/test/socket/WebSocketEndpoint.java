package org.rainbow.test.socket;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;
import org.rainbow.websocket.annotation.*;
import org.rainbow.websocket.pojo.Session;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Map;

/**
 * @author K
 * @date 2021/3/8  16:52
 */
@ServerEndpoint(path = "/ws/{arg}", port = "8898")
public class WebSocketEndpoint {

    @BeforeHandshake
    public void handshake(Session session, HttpHeaders headers,
                          @RequestParam String req, @RequestParam MultiValueMap reqMap,
                          @PathVariable String arg, @PathVariable Map pathMap) {
        System.out.println("握手成功");
    }

    @OnOpen
    public void onOpen(Session session, HttpHeaders headers,
                       @RequestParam String req, @RequestParam MultiValueMap reqMap,
                       @PathVariable String arg, @PathVariable Map pathMap) {
        System.out.println("新建连接");
        System.out.println(req);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("连接关闭");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println(message);
        session.sendText("Hello Netty!");
    }

    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
        for (byte b : bytes) {
            System.out.println(b);
        }
        session.sendBinary(bytes);
    }

    @OnEvent
    public void onEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("read idle");
                    break;
                case WRITER_IDLE:
                    System.out.println("write idle");
                    break;
                case ALL_IDLE:
                    System.out.println("all idle");
                    break;
                default:
                    break;
            }
        }
    }
}
