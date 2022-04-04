package com.github.pmoerenhout.jsmpp.web;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableAsync
public class ApplicationConfig implements AsyncConfigurer {

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

  @Bean
  public AuthenticationTrustResolver trustResolver() {
    // allow anonymous as principal/authentication for the time being, until mutual certificate is implemented
    return new AnonymousAuthenticationTrustResolverImpl();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    final PasswordEncoder encoder = new BCryptPasswordEncoder();
    return encoder;
  }

  @EventListener
  public void logEnvironment(final ApplicationReadyEvent event) {
    log.debug("Environment: {}", event.getApplicationContext().getEnvironment());
  }
}