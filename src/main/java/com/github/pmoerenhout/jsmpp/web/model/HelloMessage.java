package com.github.pmoerenhout.jsmpp.web.model;

public class HelloMessage {

  private String name;

  public HelloMessage() {
  }

  public HelloMessage(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
