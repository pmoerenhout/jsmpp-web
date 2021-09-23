package com.github.pmoerenhout.jsmpp.web.controller;

import java.security.Principal;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pmoerenhout.jsmpp.web.json.KeepAliveResponse;
import lombok.extern.slf4j.Slf4j;

// @Secured(AUTHORITY_API)
@Slf4j
@RestController
// @RequestMapping(CONTEXT_PATH_API)
public class RestApiController {

//  @Autowired
//  private SessionService sessionService;

  @RequestMapping(value = "/keepalive", method = RequestMethod.GET)
  public KeepAliveResponse getKeepAlive(final Authentication authentication, final HttpServletRequest request) {
    final String username = getUsername(authentication);
    log.info("Keepalive request from {}: {} {}", username, request.getRemoteAddr(), request.getRequestURI());
    final KeepAliveResponse keepAliveResponse = new KeepAliveResponse();
    keepAliveResponse.setTimestamp(Instant.now());
    return keepAliveResponse;
  }

  private String getUsername(final Principal principal) {
    // final User contextAuthenticationPrincipal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // principal org.springframework.security.authentication.UsernamePasswordAuthenticationToken
    // contextAuthenticationPrincipal: org.springframework.security.core.userdetails.User
    // LOG.debug("principal:{} contextAuthenticationPrincipal:{}", principal, contextAuthenticationPrincipal);
    log.debug("principal name:{}", principal.getName());
    return principal.getName();
  }

  private String getUsername(final Authentication authentication) {
    // final User contextAuthenticationPrincipal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // principal org.springframework.security.authentication.UsernamePasswordAuthenticationToken
    // contextAuthenticationPrincipal: org.springframework.security.core.userdetails.User
    // LOG.debug("principal:{} contextAuthenticationPrincipal:{}", principal, contextAuthenticationPrincipal);
    log.debug("principal:{}", authentication.getPrincipal());
    log.debug("credentials:{}", authentication.getCredentials());
    log.debug("details:{}", authentication.getDetails());
    log.debug("authorities:{}", authentication.getAuthorities());

    return authentication.getPrincipal().toString();
  }

}

