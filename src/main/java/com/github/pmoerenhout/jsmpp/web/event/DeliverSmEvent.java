package com.github.pmoerenhout.jsmpp.web.event;

import org.jsmpp.bean.DeliverSm;
import org.springframework.context.ApplicationEvent;

public class DeliverSmEvent extends ApplicationEvent {

  private String connectionId;
  private DeliverSm deliverSm;

  public DeliverSmEvent(final Object source, final String connectionId, final DeliverSm deliverSm) {
    super(source);
    this.connectionId = connectionId;
    this.deliverSm = deliverSm;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public DeliverSm getDeliverSm() {
    return deliverSm;
  }
}
