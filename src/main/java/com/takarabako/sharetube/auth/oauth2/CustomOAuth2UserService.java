package com.takarabako.sharetube.auth.oauth2;

import com.takarabako.sharetube.auth.jwt.JwtAuthenticationToken;
import com.takarabako.sharetube.model.common.Role;
import com.takarabako.sharetube.model.users.User;
import com.takarabako.sharetube.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserService userService;

  private final StringRedisTemplate stringRedisTemplate;

  public CustomOAuth2UserService(UserService userService, StringRedisTemplate stringRedisTemplate) {
    this.userService = userService;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    boolean isExist = userService.existsByEmail(oAuth2User.getAttribute("email"));

    log.info(String.valueOf(isExist));

    CustomOAuth2User user = new CustomOAuth2User(oAuth2User, !isExist);

    // 존재하는 유저의 정보로 OAuth2AuthenticationToken을 제작하여 SecurityContextHolder에 set.
    // 로그인 단계에서 JwtAuthenticationProvider의 로직은 이미 Google이 검증했으므로 생략하고 바로 set 해준다.
    JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(user.getId(),null);
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    // 토큰을 redis에 저장,
    Date expire = new Date(new Date().getTime() + 3600 * 1_000L);
    stringRedisTemplate.opsForValue().set(user.getId(), userRequest.getAccessToken().getTokenValue(), expire.getTime(), TimeUnit.MILLISECONDS);

    if (isExist) {
      // 유저가 존재할 경우 업데이트.
      User savedUser = userService.findByEmail(user.getEmail());

      savedUser.setNickname(user.getName());
      savedUser.setProfileImgUrl(user.getProfImg());

      userService.update(savedUser);

    } else {
      // 어드민 계정 생성 이메일
      if (user.getEmail().equals("icetime963@gmail.com")) {
        userService.save(new User(user.getId(), user.getEmail(), user.getName(), user.getProfImg(), Role.ADMIN));
      } else {
        userService.save(new User(user.getId(), user.getEmail(), user.getName(), user.getProfImg(), Role.USER));

      }
    }

    return user;
  }
}
