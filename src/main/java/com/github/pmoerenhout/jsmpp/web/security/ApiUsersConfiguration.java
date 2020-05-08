package com.github.pmoerenhout.jsmpp.web.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api")
public class ApiUsersConfiguration {

  private List<ApiUser> users = new ArrayList<>();

  public List<ApiUser> getUsers() {
    return this.users;
  }

  public static class ApiUser {
    private String username;
    private String password;

    public String getUsername() {
      return username;
    }

    public void setUsername(final String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(final String password) {
      this.password = password;
    }
  }
}
