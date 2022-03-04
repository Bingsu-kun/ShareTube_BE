package com.takarabako.sharetube.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
            .addMapping("/auth/**")
            .allowedOrigins("*") // 모든 origin 허용. 추후 프론트 origin에 따라 설정 필요.
            .allowedMethods("*")
            .exposedHeaders("*","authorization")
            .allowCredentials(true)
            .maxAge(3600);
    registry
            .addMapping("/login/oauth2/code/google/**")
            .allowedOrigins("*") // 모든 origin 허용. 추후 프론트 origin에 따라 설정 필요.
            .allowedMethods("*")
            .exposedHeaders("*","authorization")
            .allowCredentials(true)
            .maxAge(3600);
    registry
            .addMapping("/api/**")
            .allowedOrigins("*") // 모든 origin 허용. 추후 프론트 origin에 따라 설정 필요.
            .allowedMethods("*")
            .exposedHeaders("*","authorization")
            .allowCredentials(true)
            .maxAge(3600);

  }

}
