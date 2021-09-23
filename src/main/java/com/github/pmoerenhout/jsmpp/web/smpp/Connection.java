package com.github.pmoerenhout.jsmpp.web.smpp;

import java.nio.charset.Charset;

import org.jsmpp.session.SMPPSession;

import com.github.pmoerenhout.jsmpp.pool.PooledSMPPSession;

public final class Connection<T extends SMPPSession> {

  private String id;
  private String contextId;
  private String description;
  private String serviceType;
  private Charset charset;
  private boolean transmitable;
  private boolean longSmsEnabled;
  private int longSmsMaxSize;
  private Integer singleSmsMaxSize;
  private PooledSMPPSession<T> pooledSMPPSession;

  public Connection(final String id, final String contextId, final String description, final String serviceType, final Charset charset,
                    final boolean transmitable, final boolean longSmsEnabled,
                    final int longSmsMaxSize, final Integer singleSmsMaxSize,
                    final PooledSMPPSession<T> pooledSMPPSession) {
    this.id = id;
    this.contextId = contextId;
    this.description = description;
    this.serviceType = serviceType;
    this.charset = charset;
    this.transmitable = transmitable;
    this.longSmsEnabled = longSmsEnabled;
    this.longSmsMaxSize = longSmsMaxSize;
    this.singleSmsMaxSize = singleSmsMaxSize;
    this.pooledSMPPSession = pooledSMPPSession;
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

  public PooledSMPPSession<T> getPooledSMPPSession() {
    return pooledSMPPSession;
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
    sb.append(", pooledSMPPSession=").append(pooledSMPPSession);
    sb.append('}');
    return sb.toString();
  }
}
