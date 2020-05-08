package com.github.pmoerenhout.jsmpp.web;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ApplicationConfig implements AsyncConfigurer {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfig.class);

  @Override
  public Executor getAsyncExecutor() {
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(25);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("AsyncExecutor-");
    executor.initialize();
    return executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new MyAsyncUncaughtExceptionHandler();
  }

  @Bean
  TaskExecutor taskExecutor() {
    ConcurrentTaskExecutor taskExecutor = new ConcurrentTaskExecutor(Executors.newFixedThreadPool(25));
    return taskExecutor;
  }

//  @Bean
//  public SessionFactory sessionFactory(final HibernateEntityManagerFactory hibernateEntityManagerFactory){
//    return hibernateEntityManagerFactory.getSessionFactory();
//  }

  @EventListener
  public void logEnvironment(final ApplicationReadyEvent event) {
    LOG.debug("Environment: {}", event.getApplicationContext().getEnvironment());
  }
}