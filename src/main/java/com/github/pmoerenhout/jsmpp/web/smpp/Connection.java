package com.github.pmoerenhout.jsmpp.web.smpp;

import java.nio.charset.Charset;

import org.jsmpp.session.SMPPSession;

public final class Connection {

  private String id;
  private String contextId;
  private String description;
  private String serviceType;
  private Charset charset;
  private boolean transmitable;
  private boolean longSmsEnabled;
  private int longSmsMaxSize;
  private Integer singleSmsMaxSize;
  private SMPPSession session;

  public Connection(final String id, final String contextId, final String description, final String serviceType, final Charset charset,
                    final boolean transmitable, final boolean longSmsEnabled,
                    final int longSmsMaxSize, final Integer singleSmsMaxSize,
                    final SMPPSession session) {
    this.id = id;
    this.contextId = contextId;
    this.description = description;
    this.serviceType = serviceType;
    this.charset = charset;
    this.transmitable = transmitable;
    this.longSmsEnabled = longSmsEnabled;
    this.longSmsMaxSize = longSmsMaxSize;
    this.singleSmsMaxSize = singleSmsMaxSize;
    this.session = session;
  }

  public String getId() {
    return id;
  }

  public String getContextId() {
    return contextId;
  }

  public String getDescription() {
    return description;
  }

  public String getServiceType() {
    return serviceType;
  }

  public Charset getCharset() {
    return charset;
  }

  public boolean isTransmitable() {
    return transmitable;
  }

  public SMPPSession getSession() {
    return session;
  }

  public boolean isLongSmsEnabled() {
    return longSmsEnabled;
  }

  public int getLongSmsMaxSize() {
    return longSmsMaxSize;
  }

  public Integer getSingleSmsMaxSize() {
    return singleSmsMaxSize;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Connection{");
    sb.append("id='").append(id).append('\'');
    sb.append(", contextId='").append(contextId).append('\'');
    sb.append(", description='").append(description).append('\'');
    sb.append(", serviceType='").append(serviceType).append('\'');
    sb.append(", charset=").append(charset);
    sb.append(", transmitable=").append(transmitable);
    sb.append(", longSmsEnabled=").append(longSmsEnabled);
    sb.append(", longSmsMaxSize=").append(longSmsMaxSize);
    sb.append(", singleSmsMaxSize=").append(singleSmsMaxSize);
    sb.append(", session=").append(session);
    sb.append('}');
    return sb.toString();
  }
}
