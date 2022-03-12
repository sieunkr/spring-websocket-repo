package com.example.springwebsocketstomp;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    /*
        /sub/channel/12345      - 구독(channelId:12345)
        /pub/hello              - 메시지 발행
    */

    @MessageMapping("/hello")
    public void message(Message message) {

        
        simpMessageSendingOperations.convertAndSend("/sub/channel/" + message.getChannelId(), message);
    }
}
