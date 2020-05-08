package com.github.pmoerenhout.jsmpp.web.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyHandler extends TextWebSocketHandler {

  private static final Logger LOG = LoggerFactory.getLogger(MyHandler.class);

  @Override
  public void handleTextMessage(final WebSocketSession session, final TextMessage message) {
    LOG.info("handleTextMessage {} {}", session, message);
    LOG.info(" payload {}", message.getPayload());
  }

}
