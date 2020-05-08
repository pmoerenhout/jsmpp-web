package com.github.pmoerenhout.jsmpp.web.smpp;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmppBeanConfig {

  @Bean
  @Qualifier("sessionStateListener")
  public SessionStateListenerImpl getSessionStateListener() {
    return new SessionStateListenerImpl();
  }

}
