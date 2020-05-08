package com.github.pmoerenhout.jsmpp.web;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
  void accept(T t) throws E;
}
