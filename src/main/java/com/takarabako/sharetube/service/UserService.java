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

  @Transactional(readOnly = true)
  public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("존재하지 않는 이메일입니다. : " + email));
  }

  @Transactional
  public User save(User user){
    return userRepository.save(user);
  }

}
