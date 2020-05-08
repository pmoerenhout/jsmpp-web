package com.github.pmoerenhout.jsmpp.web;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class MyAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(MyAsyncUncaughtExceptionHandler.class);

  public void handleUncaughtException(Throwable ex, Method method, Object... params) {
    LOG.error("Asynchronous exception: {}", ex.getMessage());
    LOG.error("Asynchronous exception", ex);
  }
}
