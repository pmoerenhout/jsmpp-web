package com.github.pmoerenhout.jsmpp.web.smpp.dcs;

public class InvalidDataCodingSchemeException extends Exception {

  private static final long serialVersionUID = -1169679309429534173L;

  public InvalidDataCodingSchemeException() {
  }

  public InvalidDataCodingSchemeException(final String message) {
    super(message);
  }

  public InvalidDataCodingSchemeException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InvalidDataCodingSchemeException(final Throwable cause) {
    super(cause);
  }

  public InvalidDataCodingSchemeException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
