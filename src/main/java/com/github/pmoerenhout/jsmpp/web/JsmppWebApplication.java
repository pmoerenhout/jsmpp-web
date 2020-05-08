package com.github.pmoerenhout.jsmpp.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EntityScan("com.github.pmoerenhout.jsmpp.web.jpa")
@EnableJpaRepositories("com.github.pmoerenhout")
@EnableAsync
@SpringBootApplication
public class JsmppWebApplication extends SpringBootServletInitializer {

  public static void main(final String[] args) {
    new SpringApplicationBuilder(JsmppWebApplication.class)
        .initializers(new ServletApplicationContextInitializer())
        .headless(true)
        .run(args);
  }

//  @Bean(name = "threadPoolTaskExecutor")
//  public Executor threadPoolTaskExecutor() {
//    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
////    executor.setCorePoolSize(5);
////    executor.setMaxPoolSize(2);
////    executor.setQueueCapacity(500);
//    executor.setThreadNamePrefix("async-");
////    executor.initialize();
//    return executor;
//  }

//  @Bean(name = "messageSource")
//  public MessageSource messageSource() {
//
//    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
//    messageSource.setBasename("/");
//    messageSource.setFallbackToSystemLocale(false);
//    messageSource.setCacheSeconds(0);
//    messageSource.setDefaultEncoding("UTF-8");
//    return messageSource;
//  }

}
