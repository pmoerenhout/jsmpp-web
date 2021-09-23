package com.github.pmoerenhout.jsmpp.web;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServletApplicationContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  public ServletApplicationContextInitializer() {
  }

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) throws RuntimeException {
    log.debug("Environment: {}", applicationContext.getEnvironment());
  }

}
