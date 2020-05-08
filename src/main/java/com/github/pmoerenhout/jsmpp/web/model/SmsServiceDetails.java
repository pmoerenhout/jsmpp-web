package com.github.pmoerenhout.jsmpp.web.model;

public final class SmsServiceDetails {

  private String messageId;
  private String connectionId;

  public SmsServiceDetails(final String messageId, final String connectionId) {
    this.messageId = messageId;
    this.connectionId = connectionId;
  }

  public String getMessageId() {
    return messageId;
  }

  public String getConnectionId() {
    return connectionId;
  }
}
