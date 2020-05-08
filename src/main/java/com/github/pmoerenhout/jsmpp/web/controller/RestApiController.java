package com.github.pmoerenhout.jsmpp.web.controller;

import java.security.Principal;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pmoerenhout.jsmpp.web.json.KeepAliveResponse;

// @Secured(AUTHORITY_API)
@RestController
// @RequestMapping(CONTEXT_PATH_API)
public class RestApiController {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiController.class);

//  @Autowired
//  private SessionService sessionService;

  @RequestMapping(value = "/keepalive", method = RequestMethod.GET)
  public KeepAliveResponse getKeepAlive(final Authentication authentication, final HttpServletRequest request) {
    final String username = getUsername(authentication);
    LOG.info("Keepalive request from {}: {} {}", username, request.getRemoteAddr(), request.getRequestURI());
    final KeepAliveResponse keepAliveResponse = new KeepAliveResponse();
    keepAliveResponse.setTimestamp(Instant.now());
    return keepAliveResponse;
  }

  private String getUsername(final Principal principal) {
    // final User contextAuthenticationPrincipal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // principal org.springframework.security.authentication.UsernamePasswordAuthenticationToken
    // contextAuthenticationPrincipal: org.springframework.security.core.userdetails.User
    // LOG.debug("principal:{} contextAuthenticationPrincipal:{}", principal, contextAuthenticationPrincipal);
    LOG.debug("principal name:{}", principal.getName());
    return principal.getName();
  }

  private String getUsername(final Authentication authentication) {
    // final User contextAuthenticationPrincipal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // principal org.springframework.security.authentication.UsernamePasswordAuthenticationToken
    // contextAuthenticationPrincipal: org.springframework.security.core.userdetails.User
    // LOG.debug("principal:{} contextAuthenticationPrincipal:{}", principal, contextAuthenticationPrincipal);
    LOG.debug("principal:{}", authentication.getPrincipal());
    LOG.debug("credentials:{}", authentication.getCredentials());
    LOG.debug("details:{}", authentication.getDetails());
    LOG.debug("authorities:{}", authentication.getAuthorities());

    return authentication.getPrincipal().toString();
  }

}

