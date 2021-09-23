package com.github.pmoerenhout.jsmpp.web;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

  public void handleUncaughtException(Throwable ex, Method method, Object... params) {
    log.error("Asynchronous exception: {}", ex.getMessage());
    log.error("Asynchronous exception", ex);
  }
}
