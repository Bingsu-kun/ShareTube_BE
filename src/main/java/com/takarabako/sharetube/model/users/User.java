package com.takarabako.sharetube.model.users;

import com.takarabako.sharetube.auth.jwt.Jwt;
import com.takarabako.sharetube.model.common.Role;
import com.takarabako.sharetube.model.lists.ShareList;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
  @Column(name = "oauth2_id")
  private String oAuth2Id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String nickname;

  private String profileImgUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @OneToMany(mappedBy = "author")
  private List<ShareList> myLists;

  @OneToMany
  @JoinColumn(name = "lists_id")
  private List<ShareList> subscribing;

  private int lastTotal;

  public User(String oAuth2Id, String email, String nickname, String profileImgUrl, Role role) {
    this(oAuth2Id, email, nickname, profileImgUrl, role, new ArrayList<ShareList>(), new ArrayList<ShareList>(), 0);
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public void setLastTotal(int total) { this.lastTotal = total; }

  public void setProfileImgUrl(String profileImgUrl) {
    this.profileImgUrl = profileImgUrl;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public String newAccessToken(Jwt jwt, String[] roles) { return jwt.newAccessToken(Jwt.Claims.of(oAuth2Id,email,roles)); }

}
