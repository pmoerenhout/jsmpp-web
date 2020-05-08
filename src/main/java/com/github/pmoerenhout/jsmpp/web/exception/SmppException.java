package com.github.pmoerenhout.jsmpp.web.exception;

public class SmppException extends Exception {

  private static final long serialVersionUID = 6551832225899005424L;

  public SmppException(final String message) {
    super(message);
  }

  public SmppException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public SmppException(final Throwable cause) {
    super(cause);
  }

  public SmppException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}