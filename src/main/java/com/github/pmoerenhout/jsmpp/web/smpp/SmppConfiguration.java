package com.github.pmoerenhout.jsmpp.web.smpp;

import java.nio.charset.Charset;
import java.util.List;

import org.jsmpp.bean.BindType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.github.pmoerenhout.jsmpp.web.charset.CharsetService;

@Configuration
@ConfigurationProperties(prefix = "smpp")
public class SmppConfiguration {

  private List<SmppConnection> connections;

  public SmppConfiguration() {
  }

  public SmppConfiguration(List<SmppConnection> connections) {
    this.connections = connections;
  }

  public List<SmppConnection> getConnections() {
    return connections;
  }

  public void setConnections(final List<SmppConnection> connections) {
    this.connections = connections;
  }

  public static class SmppConnection {

    private String id;
    private String contextId;
    private String description;
    private boolean enabled = true;
    private String serviceType;
    private Charset charset;
    private String host;
    private int port;
    private boolean ssl;
    private String systemId;
    private String password;
    private String systemType;
    private BindType bindType = BindType.BIND_TRX;
    private int bindTimeout;
    private int enquireLinkTimer;
    private int transactionTimer;

    private int poolMaxTotal;
    private int poolMinIdle;
    private int poolMaxIdle;
    private int poolRate;
    private int maxConcurrentRequests;
    private int pduProcessorDegree;

    private boolean longSmsEnabled = false;
    private int longSmsMaxSize;
    private Integer singleSmsMaxSize;

    public SmppConnection() {
    }

    public String getId() {
      return id;
    }

    public void setId(final String id) {
      this.id = id;
    }

    public String getContextId() {
      return contextId;
    }

    public void setContextId(final String contextId) {
      this.contextId = contextId;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(final String description) {
      this.description = description;
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(final boolean enabled) {
      this.enabled = enabled;
    }

    public String getServiceType() {
      return serviceType;
    }

    public void setServiceType(final String serviceType) {
      this.serviceType = serviceType;
    }

    public Charset getCharset() {
      return charset;
    }

    public void setCharset(final String charset) {
      this.charset = CharsetService.getCharset(charset);
    }

    public String getHost() {
      return host;
    }

    public void setHost(final String host) {
      this.host = host;
    }

    public int getPort() {
      return port;
    }

    public void setPort(final int port) {
      this.port = port;
    }

    public boolean isSsl() {
      return ssl;
    }

    public void setSsl(final boolean ssl) {
      this.ssl = ssl;
    }

    public String getSystemId() {
      return systemId;
    }

    public void setSystemId(final String systemId) {
      this.systemId = systemId;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(final String password) {
      this.password = password;
    }

    public String getSystemType() {
      return systemType;
    }

    public void setSystemType(final String systemType) {
      this.systemType = systemType;
    }

    public BindType getBindType() {
      return bindType;
    }

    public void setBindType(final BindType bindType) {
      this.bindType = bindType;
    }

    public int getBindTimeout() {
      return bindTimeout;
    }

    public void setBindTimeout(final int bindTimeout) {
      this.bindTimeout = bindTimeout;
    }

    public int getEnquireLinkTimer() {
      return enquireLinkTimer;
    }

    public void setEnquireLinkTimer(final int enquireLinkTimer) {
      this.enquireLinkTimer = enquireLinkTimer;
    }

    public int getTransactionTimer() {
      return transactionTimer;
    }

    public void setTransactionTimer(final int transactionTimer) {
      this.transactionTimer = transactionTimer;
    }

    public int getPoolMaxTotal() {
      return poolMaxTotal;
    }

    public void setPoolMaxTotal(final int poolMaxTotal) {
      this.poolMaxTotal = poolMaxTotal;
    }

    public int getPoolMinIdle() {
      return poolMinIdle;
    }

    public void setPoolMinIdle(final int poolMinIdle) {
      this.poolMinIdle = poolMinIdle;
    }

    public int getPoolMaxIdle() {
      return poolMaxIdle;
    }

    public void setPoolMaxIdle(final int poolMaxIdle) {
      this.poolMaxIdle = poolMaxIdle;
    }

    public int getPoolRate() {
      return poolRate;
    }

    public void setPoolRate(final int poolRate) {
      this.poolRate = poolRate;
    }

    public int getMaxConcurrentRequests() {
      return maxConcurrentRequests;
    }

    public void setMaxConcurrentRequests(final int maxConcurrentRequests) {
      this.maxConcurrentRequests = maxConcurrentRequests;
    }

    public int getPduProcessorDegree() {
      return pduProcessorDegree;
    }

    public void setPduProcessorDegree(final int pduProcessorDegree) {
      this.pduProcessorDegree = pduProcessorDegree;
    }

    public boolean isLongSmsEnabled() {
      return longSmsEnabled;
    }

    public void setLongSmsEnabled(final boolean longSmsEnabled) {
      this.longSmsEnabled = longSmsEnabled;
    }

    public int getLongSmsMaxSize() {
      return longSmsMaxSize;
    }

    public void setLongSmsMaxSize(final int longSmsMaxSize) {
      this.longSmsMaxSize = longSmsMaxSize;
    }

    public Integer getSingleSmsMaxSize() {
      return singleSmsMaxSize;
    }

    public void setSingleSmsMaxSize(final Integer singleSmsMaxSize) {
      this.singleSmsMaxSize = singleSmsMaxSize;
    }
  }
}
