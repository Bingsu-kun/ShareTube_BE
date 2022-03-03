package com.takarabako.sharetube.service;

import com.takarabako.sharetube.error.NotFoundException;
import com.takarabako.sharetube.model.users.User;
import com.takarabako.sharetube.repository.UserRepository;
import com.takarabako.sharetube.util.RedisUtils;
import com.takarabako.sharetube.util.YoutubeUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;


@Service
public class UserService {

  private final UserRepository userRepository;
  private final RedisUtils redisUtils;
  private final YoutubeUtils youtubeUtils;

  public UserService(UserRepository userRepository, RedisUtils redisUtils, YoutubeUtils youtubeUtils) {
    this.userRepository = userRepository;
    this.redisUtils = redisUtils;
    this.youtubeUtils = youtubeUtils;
  }

  // DB Access

  @Transactional(readOnly = true)
  public User findByOAuth2Id(String oAuth2Id) {
    return userRepository.findByOAuth2Id(oAuth2Id).orElseThrow(() -> new NotFoundException("존재하지 않는 유저 ID입니다. sub: " + oAuth2Id));
  }

  @Transactional(readOnly = true)
  public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("존재하지 않는 유저 이메일입니다. email: " + email));
  }

  @Transactional(readOnly = true)
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  @Transactional
  public void save(User user){
    userRepository.save(user);
  }

  @Transactional
  public void update(User user) { userRepository.save(user); }

  // Youtube API

  public HashMap<String,Object> getSubscriptions(String userId) {
    // 구글 OAuth2 인증 시 가져온 AccessToken을 Redis에서 꺼내옵니다.
    // 이 과정에서 Redis에 토큰이 존재하지 않을 시 유효기간이 만료되어 사라진 것 이므로 RedisUtils에서 AccessTokenExpiredException을 뱉습니다.
    String accessToken = redisUtils.getAccessToken(userId);

    return youtubeUtils.getSubscriptions(userId,accessToken);
  }

}
