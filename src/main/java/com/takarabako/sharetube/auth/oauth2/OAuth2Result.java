package com.takarabako.sharetube.auth.oauth2;

import com.takarabako.sharetube.controller.UserDto;
import com.takarabako.sharetube.model.common.ApiError;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;

@ToString
@Getter
public class OAuth2Result {

  private final String accessToken;

  private final UserDto user;

  private final HashMap<String,Object> youtubeSubs;

  private Boolean isNew;

  public OAuth2Result(String accessToken, UserDto user) {
    this(accessToken,user,null,false);
  }

  public OAuth2Result(String accessToken, UserDto user, HashMap<String,Object> youtubeSubs, Boolean isNew) {
    if (accessToken == null)
      throw new IllegalArgumentException("accessToken must be provided");
    else if (user == null)
      throw new IllegalArgumentException("user must be provided");

    this.accessToken = accessToken;
    this.user = user;
    this.youtubeSubs = youtubeSubs;
    this.isNew = isNew;
  }

  public void setIsNew(boolean isNew) {
    this.isNew = isNew;
  }
}
