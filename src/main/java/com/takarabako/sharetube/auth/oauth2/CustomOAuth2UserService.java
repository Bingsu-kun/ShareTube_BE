package com.takarabako.sharetube.auth.oauth2;

import com.takarabako.sharetube.error.NotFoundException;
import com.takarabako.sharetube.model.common.Role;
import com.takarabako.sharetube.model.users.User;
import com.takarabako.sharetube.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private UserService userService;

  public CustomOAuth2UserService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    CustomOAuth2User user = new CustomOAuth2User(oAuth2User);
    log.info(userRequest.getAccessToken().getTokenValue());
    // 구글의 AccessToken 만료시간은 1시간.
//    log.info(String.valueOf(LocalDateTime.now()));
//    log.info(String.valueOf(LocalDateTime.ofInstant(userRequest.getAccessToken().getExpiresAt(), ZoneId.of("Asia/Seoul"))));
    log.info(String.valueOf(oAuth2User.getAttributes()));

    try {
      User savedUser = userService.findByEmail(user.getEmail());
      // 유저가 존재할 경우 업데이트.
      savedUser.setNickname(user.getName());
      savedUser.setProfileImgUrl(user.getProfImg());
    } catch (NotFoundException notFoundException) {
      // 유저가 존재하지 않을 경우 새로 생성.
      userService.save(new User(user.getId(), user.getEmail(), user.getName(), user.getProfImg(), Role.USER));
    }

    return user;
  }
}
