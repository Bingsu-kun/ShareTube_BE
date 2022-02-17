package com.takarabako.sharetube.service;

import com.takarabako.sharetube.error.NotFoundException;
import com.takarabako.sharetube.model.users.User;
import com.takarabako.sharetube.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

  private UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
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

  // Youtube Api 이용해서 구독 목록 받아오는 로직

}
