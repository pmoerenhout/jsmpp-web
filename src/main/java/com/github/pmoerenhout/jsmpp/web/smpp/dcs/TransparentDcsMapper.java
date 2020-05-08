package com.github.pmoerenhout.jsmpp.web.smpp.dcs;

public class TransparentDcsMapper implements DcsMapper {

  public byte map(final byte dcs) {
    return dcs;
  }
}
