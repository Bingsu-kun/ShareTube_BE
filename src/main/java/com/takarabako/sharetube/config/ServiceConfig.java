package com.takarabako.sharetube.config;

import com.takarabako.sharetube.auth.jwt.Jwt;
import com.takarabako.sharetube.util.MessageUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ServiceConfig {

  @Bean
  public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
    MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
    MessageUtils.setMessageSourceAccessor(messageSourceAccessor);
    return messageSourceAccessor;
  }

  @Bean
  public Jwt jwt(JwtConfig jwtConfig) {
    return new Jwt(jwtConfig.getIssuer(), jwtConfig.getClientSecret(), jwtConfig.getExpirySeconds());
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

}
