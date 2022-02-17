package com.takarabako.sharetube.auth.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class  CustomOAuth2User implements OAuth2User {

  private final OAuth2User oAuth2User;

  private boolean isNew;

  public CustomOAuth2User(OAuth2User oAuth2User, boolean isNew) {
    this.oAuth2User = oAuth2User;
    this.isNew = isNew;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return oAuth2User.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return oAuth2User.getAuthorities();
  }

  public String getName() {
    return oAuth2User.getAttribute("name");
  }

  public String getId() { return oAuth2User.getAttribute("sub"); }

  public String getEmail() {
    return oAuth2User.getAttribute("email");
  }

  public String getProfImg() { return oAuth2User.getAttribute("picture"); }

  public boolean isNew() { return isNew; }

}
