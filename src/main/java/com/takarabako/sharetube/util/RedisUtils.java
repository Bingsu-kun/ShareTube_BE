package com.takarabako.sharetube.util;

import com.takarabako.sharetube.error.AccessTokenExpiredException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtils {

  private final StringRedisTemplate stringRedisTemplate;

  public RedisUtils(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public String getAccessToken(String userId) {
    String accessToken = stringRedisTemplate.opsForValue().get(userId);
    if (accessToken == null)
      throw new AccessTokenExpiredException("AccessToken is expired. re-login required.");

    return accessToken;
  }

}
