package com.takarabako.sharetube.controller.user;

import com.takarabako.sharetube.model.users.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserDto {

  private String id;

  private String nickname;

  private String email;

  private String profile;

  private String role;

  public UserDto(User user) {
    this.id = user.getOAuth2Id();
    this.email = user.getEmail();
    this.nickname = user.getNickname();
    this.profile = user.getProfileImgUrl();
    this.role = user.getRole().name();
  }
}
