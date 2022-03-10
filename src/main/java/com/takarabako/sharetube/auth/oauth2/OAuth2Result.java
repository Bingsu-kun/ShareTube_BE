package com.takarabako.sharetube.auth.oauth2;

import com.takarabako.sharetube.controller.user.UserDto;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;

@ToString
@Getter
public class OAuth2Result {

  private final String accessToken;

  private final UserDto user;

  private final HashMap<String,Object> youtubeSubs;

  public OAuth2Result(String accessToken, UserDto user) {
    this(accessToken,user,null);
  }

  public OAuth2Result(String accessToken, UserDto user, HashMap<String,Object> youtubeSubs) {
    if (accessToken == null)
      throw new IllegalArgumentException("accessToken must be provided");
    else if (user == null)
      throw new IllegalArgumentException("user must be provided");

    this.accessToken = accessToken;
    this.user = user;
    this.youtubeSubs = youtubeSubs;
  }

}
