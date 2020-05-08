package com.github.pmoerenhout.jsmpp.web.event;

import org.springframework.context.ApplicationEvent;

import com.github.pmoerenhout.jsmpp.web.jpa.model.SmIn;

public class SmInEvent extends ApplicationEvent {

  private String connectionId;
  private SmIn sm;

  public SmInEvent(final Object source, final String connectionId, final SmIn sm) {
    super(source);
    this.connectionId = connectionId;
    this.sm = sm;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public SmIn getSm() {
    return sm;
  }
}
