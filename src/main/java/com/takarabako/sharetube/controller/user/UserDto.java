package com.takarabako.sharetube.controller.user;

import com.takarabako.sharetube.model.users.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserDto {

  private String userId;

  private String email;

  private String name;

  private String profImgUrl;

  private String userRole;

  public UserDto(User user) {
    this.userId = user.getOAuth2Id();
    this.email = user.getEmail();
    this.name = user.getNickname();
    this.profImgUrl = user.getProfileImgUrl();
    this.userRole = user.getRole().name();
  }
}
