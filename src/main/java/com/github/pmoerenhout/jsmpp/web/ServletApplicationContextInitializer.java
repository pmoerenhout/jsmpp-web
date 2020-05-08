package com.github.pmoerenhout.jsmpp.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ServletApplicationContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final Logger LOG = LoggerFactory.getLogger(ServletApplicationContextInitializer.class);

  public ServletApplicationContextInitializer() {
  }

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) throws RuntimeException {
    LOG.debug("Environment: {}", applicationContext.getEnvironment());
  }

}
