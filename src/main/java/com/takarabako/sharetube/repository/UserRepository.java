package com.takarabako.sharetube.repository;

import com.takarabako.sharetube.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  @Query(value = "SELECT * FROM users WHERE oauth2_id = :oauth2_id", nativeQuery = true)
  Optional<User> findByOAuth2Id(@Param(value = "oauth2_id") String oAuth2Id);

  @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
  Optional<User> findByEmail(@Param(value = "email") String email);

  @Query(value = "SELECT COUNT(*) > 0 FROM users WHERE email = :email LIMIT 1", nativeQuery = true)
  boolean existsByEmail(@Param(value = "email") String email);

}
