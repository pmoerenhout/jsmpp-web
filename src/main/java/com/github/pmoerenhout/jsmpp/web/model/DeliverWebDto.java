package com.github.pmoerenhout.jsmpp.web.model;

import java.time.ZonedDateTime;

public class DeliverWebDto {

  private String pdu;

  private String serviceType;
  private ZonedDateTime smscTimestamp;
  private String smscAddress;

  private byte dataCodingScheme;
  private byte protocolId;

  private String originatorAddress;

  private boolean moreMessagesToSend;
  private boolean userDataHeaderIndicator;
  private boolean replyPath;
  private String userDataHeader;
  private String message;

  public DeliverWebDto(final String pdu, final String serviceType, final ZonedDateTime smscTimestamp, final String smscAddress, final byte dataCodingScheme, final byte protocolId, final String originatorAddress,
                       final boolean moreMessagesToSend,
                       final boolean userDataHeaderIndicator, final boolean replyPath, final String userDataHeader, final String message) {
    this.pdu = pdu;
    this.serviceType = serviceType;
    this.smscTimestamp = smscTimestamp;
    this.smscAddress = smscAddress;
    this.dataCodingScheme = dataCodingScheme;
    this.protocolId = protocolId;
    this.originatorAddress = originatorAddress;
    this.moreMessagesToSend = moreMessagesToSend;
    this.userDataHeaderIndicator = userDataHeaderIndicator;
    this.replyPath = replyPath;
    this.userDataHeader = userDataHeader;
    this.message = message;
  }

  public String getPdu() {
    return pdu;
  }

  public ZonedDateTime getSmscTimestamp() {
    return smscTimestamp;
  }

  public String getServiceType() {
    return serviceType;
  }

  public String getSmscAddress() {
    return smscAddress;
  }

  public byte getDataCodingScheme() {
    return dataCodingScheme;
  }

  public byte getProtocolId() {
    return protocolId;
  }

  public String getOriginatorAddress() {
    return originatorAddress;
  }

  public boolean isMoreMessagesToSend() {
    return moreMessagesToSend;
  }

  public boolean isUserDataHeaderIndicator() {
    return userDataHeaderIndicator;
  }

  public boolean isReplyPath() {
    return replyPath;
  }

  public String getUserDataHeader() {
    return userDataHeader;
  }

  public String getMessage() {
    return message;
  }

}
