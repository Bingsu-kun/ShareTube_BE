package com.takarabako.sharetube.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@ToString
@Getter
public class Jwt {

  private final String issuer;

  private final String clientSecret;

  private final int expirySeconds;

  private final Algorithm algorithm;

  private final JWTVerifier jwtVerifier;

  public Jwt(String issuer, String clientSecret, int expirySeconds) {
    this.issuer = issuer;
    this.clientSecret = clientSecret;
    if (expirySeconds > 0)
      this.expirySeconds = expirySeconds;
    else
      this.expirySeconds = 3600;
    this.algorithm = Algorithm.HMAC512(clientSecret);
    this.jwtVerifier = com.auth0.jwt.JWT.require(algorithm).withIssuer(issuer).build();
  }

  public String newAccessToken(Claims claims) {
    Date now = new Date();
    return JWT.create().withIssuer("ShareTube")
            .withIssuedAt(now)
            .withExpiresAt(new Date(now.getTime() + expirySeconds * 1_000L))
            .withClaim("id",claims.id)
            .withClaim("email",claims.email)
            .withArrayClaim("roles",claims.roles)
            .sign(algorithm);
  }

  public String refreshToken(String token) throws JWTVerificationException {
    Claims claims = new Claims(jwtVerifier.verify(token));
    return newAccessToken(claims);
  }

  static public class Claims {
    String id;
    String email;
    String[] roles;
    Date iat;
    Date exp;

    private Claims() {
    }

    Claims(DecodedJWT decodedJWT) {
      Claim id = decodedJWT.getClaim("id");
      if (!id.isNull())
        this.id = id.asString();
      Claim email = decodedJWT.getClaim("email");
      if (!email.isNull())
        this.email = email.asString();
      Claim roles = decodedJWT.getClaim("roles");
      if (!roles.isNull())
        this.roles = roles.asArray(String.class);
      this.iat = decodedJWT.getIssuedAt();
      this.exp = decodedJWT.getExpiresAt();
    }

    public static Claims of(String id, String email, String[] roles) {
      Claims claims = new Claims();
      claims.id = id;
      claims.email = email;
      claims.roles = roles;
      return claims;
    }

    long iat() {
      return iat != null ? iat.getTime() : -1;
    }

    long exp() {
      return exp != null ? exp.getTime() : -1;
    }

    void eraseIat() {
      iat = null;
    }

    void eraseExp() {
      exp = null;
    }
  }
}
