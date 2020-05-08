package com.github.pmoerenhout.jsmpp.web.json;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

// @ApiModel(description = "The keepalive response")
public class KeepAliveResponse {

  //@ApiModelProperty(notes = "The actual server timestamp")
  @JsonProperty("timestamp")
  private Instant timestamp;

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(final Instant timestamp) {
    this.timestamp = timestamp;
  }
}