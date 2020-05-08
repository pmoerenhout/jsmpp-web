package com.github.pmoerenhout.jsmpp.web.smpp.dcs;

public interface DcsMapper {

  byte map(byte dcs) throws InvalidDataCodingSchemeException;

}
