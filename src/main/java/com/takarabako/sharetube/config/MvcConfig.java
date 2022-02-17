package com.takarabako.sharetube.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class MvcConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
            .addMapping("/api/**")
            .allowedOrigins("*") // 모든 origin 허용. 추후 프론트 origin에 따라 설정 필요.
            .allowedMethods("*")
            .exposedHeaders("*","authorization")
            .maxAge(3600);
    //           .allowCredentials(true); 쿠키 사용시 추가.
  }

}
