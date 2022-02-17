package com.takarabako.sharetube.model.users;

import com.takarabako.sharetube.auth.jwt.Jwt;
import com.takarabako.sharetube.model.common.Role;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
  @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "oauth2_id", unique = true, nullable = false)
  private String oAuth2Id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String nickname;

  private String profileImgUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  public User(String oAuth2Id, String email, String nickname, String profileImgUrl, Role role) {
    this(null, oAuth2Id, email, nickname, profileImgUrl, role);
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

  public String newAccessToken(Jwt jwt, String[] roles) { return jwt.newAccessToken(Jwt.Claims.of(oAuth2Id,email,roles)); }

}
