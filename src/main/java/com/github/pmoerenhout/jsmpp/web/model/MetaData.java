package com.github.pmoerenhout.jsmpp.web.model;

public final class MetaData {

  private String username;
  private String uri;
  private String remoteAddress;

  public MetaData(final String username, final String uri, final String remoteAddress) {
    this.username = username;
    this.uri = uri;
    this.remoteAddress = remoteAddress;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(final String uri) {
    this.uri = uri;
  }

  public String getRemoteAddress() {
    return remoteAddress;
  }

  public void setRemoteAddress(final String remoteAddress) {
    this.remoteAddress = remoteAddress;
  }
}
