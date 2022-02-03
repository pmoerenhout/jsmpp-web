package com.github.pmoerenhout.jsmpp.web;

import static com.github.pmoerenhout.jsmpp.web.JsmppWebConstants.AUTHORITY_API;
import static com.github.pmoerenhout.jsmpp.web.JsmppWebConstants.ROLE_API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = false)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  // https://gerrydevstory.com/tag/spring-security/

  @Autowired
  //@Qualifier("userDetailsService")
  private UserDetailsService userDetailsService;

  @Bean(name = "sessionRegistry")
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .antMatcher("/**")
        .headers().disable()
        .csrf().disable()
        .sessionManagement().disable()
        .anonymous().key("anonymousAuth").principal("anonymous").authorities(AUTHORITY_API).and()
        .authorizeRequests()
        .antMatchers(HttpMethod.GET, "/show").permitAll()
        .antMatchers(HttpMethod.GET, "/v2/api-docs").permitAll()
        .antMatchers(HttpMethod.GET, "/swagger-resources").permitAll()
        .antMatchers(HttpMethod.GET, "/configuration/**").permitAll()
        .antMatchers(HttpMethod.GET, "/documentation/**").permitAll()
        .antMatchers("/session").hasAnyRole(ROLE_API)
        .antMatchers("/sms/*").hasAnyRole(ROLE_API)
        .anyRequest().fullyAuthenticated().and()
        .httpBasic().realmName("This REST API requires Basic HTTP authorization");
  }

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

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    //auth.inMemoryAuthentication().withUser("theonlyone").password("secret").roles("API");
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }
}