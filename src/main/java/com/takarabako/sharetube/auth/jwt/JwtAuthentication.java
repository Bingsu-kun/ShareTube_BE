package com.takarabako.sharetube.auth.jwt;

import lombok.ToString;

@ToString
public class JwtAuthentication {

  public final String userId;

  public final String email;

  public JwtAuthentication(String userId, String email) {
    if (userId == null) {
      throw new IllegalArgumentException("id must be provided");
    }
    else if (email == null) {
      throw new IllegalArgumentException("email must be provided");
    }

    this.userId = userId;
    this.email = email;
  }

}
