package com.github.pmoerenhout.jsmpp.web.smpp.dcs;

public class DcsMapperFactory {

  private static final DcsMapper MAPPER_TRANSPARENT = new TransparentDcsMapper();
  private static final DcsMapper MAPPER_GSM = new GsmDcsMapper();

  public static DcsMapper getInstance(final DcsMap dcsMap){
    switch (dcsMap){
      case TRANSPARENT:
        return MAPPER_TRANSPARENT;
      case GSM:
        return MAPPER_GSM;
      default:
        throw new RuntimeException("No DCS mapper defined for " + dcsMap);
    }
  }
}
