package com.github.pmoerenhout.jsmpp.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AnonymousAuthenticationTrustResolverImpl implements AuthenticationTrustResolver {
  // ~ Instance fields
  // ================================================================================================

  private Class<? extends Authentication> anonymousClass = AnonymousAuthenticationToken.class;
  private Class<? extends Authentication> rememberMeClass = RememberMeAuthenticationToken.class;

  // ~ Methods
  // ========================================================================================================

  Class<? extends Authentication> getAnonymousClass() {
    return anonymousClass;
  }

  Class<? extends Authentication> getRememberMeClass() {
    return rememberMeClass;
  }

  public boolean isAnonymous(Authentication authentication) {
    if ((anonymousClass == null) || (authentication == null)) {
      return false;
    }

    //return anonymousClass.isAssignableFrom(authentication.getClass());
    return false;
  }

  public boolean isRememberMe(Authentication authentication) {
    if ((rememberMeClass == null) || (authentication == null)) {
      return false;
    }

    return rememberMeClass.isAssignableFrom(authentication.getClass());
  }

  public void setAnonymousClass(Class<? extends Authentication> anonymousClass) {
    this.anonymousClass = anonymousClass;
  }

  public void setRememberMeClass(Class<? extends Authentication> rememberMeClass) {
    this.rememberMeClass = rememberMeClass;
  }
}
