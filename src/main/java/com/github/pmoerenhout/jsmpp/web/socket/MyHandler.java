package com.github.pmoerenhout.jsmpp.web.socket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyHandler extends TextWebSocketHandler {

  @Override
  public void handleTextMessage(final WebSocketSession session, final TextMessage message) {
    log.info("handleTextMessage {} {}", session, message);
    log.info(" payload {}", message.getPayload());
  }

}
