package com.github.pmoerenhout.jsmpp.web.exception;

public class LongSmsException extends Exception {

  private static final long serialVersionUID = -8749192429207721480L;

  public LongSmsException() {
  }

  public LongSmsException(final String message) {
    super(message);
  }

  public LongSmsException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public LongSmsException(final Throwable cause) {
    super(cause);
  }

  public LongSmsException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}