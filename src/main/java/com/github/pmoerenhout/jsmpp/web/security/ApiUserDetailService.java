package com.github.pmoerenhout.jsmpp.web.security;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApiUserDetailService implements UserDetailsService {

  @Autowired
  private ApiUsersConfiguration apiUsersConfiguration;

  @Override
  public UserDetails loadUserByUsername(final String appUserName) throws UsernameNotFoundException {

    final User user = getUserByUsername(appUserName);
    if (user == null) {
      throw new UsernameNotFoundException(appUserName);
    }
    return user;
  }

  public User getUserByUsername(final String username) {
    for (final ApiUsersConfiguration.ApiUser user : apiUsersConfiguration.getUsers()) {
      if (username.equals(user.getUsername())) {
        return new User(user.getUsername(), user.getPassword(), getAuthorities());
      }
    }
    // if username is not found, return null
    return null;
  }

  private List<SimpleGrantedAuthority> getAuthorities() {
    // final List<SimpleGrantedAuthority> authList = new ArrayList<>();
    // authList.add(new SimpleGrantedAuthority("ROLE_API"));
    // return authList;
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_API"));
  }

}