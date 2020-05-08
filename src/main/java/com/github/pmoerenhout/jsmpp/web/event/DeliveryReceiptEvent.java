package com.github.pmoerenhout.jsmpp.web.event;

import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.springframework.context.ApplicationEvent;

public class DeliveryReceiptEvent extends ApplicationEvent {

  private String connectionId;
  private DeliveryReceipt deliveryReceipt;
  private DeliverSm deliverSm;

  public DeliveryReceiptEvent(final Object source, final String connectionId, final DeliveryReceipt deliveryReceipt, final DeliverSm deliverSm) {
    super(source);
    this.connectionId = connectionId;
    this.deliveryReceipt = deliveryReceipt;
    this.deliverSm = deliverSm;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(final String connectionId) {
    this.connectionId = connectionId;
  }

  public DeliveryReceipt getDeliveryReceipt() {
    return deliveryReceipt;
  }

  public void setDeliveryReceipt(final DeliveryReceipt deliveryReceipt) {
    this.deliveryReceipt = deliveryReceipt;
  }

  public DeliverSm getDeliverSm() {
    return deliverSm;
  }

  public void setDeliverSm(final DeliverSm deliverSm) {
    this.deliverSm = deliverSm;
  }
}
