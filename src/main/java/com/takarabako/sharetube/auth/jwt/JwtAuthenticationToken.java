package com.takarabako.sharetube.auth.jwt;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@ToString
@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private final Object principal;

  private String credentials;

  public JwtAuthenticationToken(String principal, String credentials) {
    super(null);
    super.setAuthenticated(false);

    this.principal = principal;
    this.credentials = credentials;
  }

  public JwtAuthenticationToken(Object principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    super.setAuthenticated(true);

    this.principal = principal;
    this.credentials = credentials;
  }

  public String authenticationRequest() {
    return String.valueOf(principal);
  }

  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    if (isAuthenticated) {
      throw new IllegalArgumentException("Cannot set this token to trusted");
    }
    super.setAuthenticated(false);
  }

  @Override
  public void eraseCredentials() {
    super.eraseCredentials();
    credentials = null;
  }

}

