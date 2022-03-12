package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    //웹소켓 연결 시
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        var sessionId = session.getId();
        sessions.put(sessionId, session);

        Message message = Message.builder().sender(sessionId).receiver("all").build();
        message.newConnect();

        sessions.values().forEach(s -> {
            try {
                if(!s.getId().equals(sessionId)) {
                    s.sendMessage(new TextMessage(Utils.getString(message)));
                }
            }
            catch (Exception e) {
                //TODO: throw
            }
        });
    }

    //양방향 데이터 통신
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {

        Message message = Utils.getObject(textMessage.getPayload());
        message.setSender(session.getId());

        WebSocketSession receiver = sessions.get(message.getReceiver());

        if (receiver != null && receiver.isOpen()) {

            receiver.sendMessage(new TextMessage(Utils.getString(message)));
        }
    }

    //소켓 연결 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        var sessionId = session.getId();

        sessions.remove(sessionId);

        final Message message = new Message();
        message.closeConnect();
        message.setSender(sessionId);

        sessions.values().forEach(s -> {
            try {
                s.sendMessage(new TextMessage(Utils.getString(message)));
            } catch (Exception e) {
                //TODO: throw
            }
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        //TODO:
    }
}
