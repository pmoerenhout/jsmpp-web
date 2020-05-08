package com.github.pmoerenhout.jsmpp.web.exception;

public class ConnectionNotFoundException extends SmppException {

  private static final long serialVersionUID = -7813420143962809949L;

  public ConnectionNotFoundException(final String message) {
    super(message);
  }

  public ConnectionNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConnectionNotFoundException(final Throwable cause) {
    super(cause);
  }

  public ConnectionNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}