package com.takarabako.sharetube.model.users;

import com.takarabako.sharetube.model.common.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
  @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
  @Column(name = "user_id")
  private final Long userId;

  @Column(name = "oauth2_id", unique = true, nullable = false)
  private final String oAuth2Id;

  @Column(unique = true, nullable = false)
  private final String email;

  @Column(nullable = false)
  private String nickname;

  private String profileImgUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  public User(String oAuth2Id, String email, String nickname, String profileImgUrl, Role role) {
    this(null, oAuth2Id, email, nickname, profileImgUrl, role);
  }

  public User(Long userId, String oAuth2Id, String email, String nickname, String profileImgUrl, Role role) {
    this.userId = userId;
    this.oAuth2Id = oAuth2Id;
    this.email = email;
    this.nickname = nickname;
    this.profileImgUrl = profileImgUrl;
    this.role = role;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public void setProfileImgUrl(String profileImgUrl) {
    this.profileImgUrl = profileImgUrl;
  }

  public void setRole(Role role) {
    this.role = role;
  }

}
