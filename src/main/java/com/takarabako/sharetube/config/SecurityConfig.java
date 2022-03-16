package com.takarabako.sharetube.config;

import com.takarabako.sharetube.auth.CustomAccessDeniedHandler;
import com.takarabako.sharetube.auth.jwt.Jwt;
import com.takarabako.sharetube.auth.jwt.JwtAuthenticationProvider;
import com.takarabako.sharetube.auth.jwt.JwtAuthenticationTokenFilter;
import com.takarabako.sharetube.auth.oauth2.CustomOAuth2FailureHandler;
import com.takarabako.sharetube.auth.oauth2.CustomOAuth2SuccessHandler;
import com.takarabako.sharetube.auth.oauth2.CustomOAuth2UserService;
import com.takarabako.sharetube.auth.EntryPointUnauthorizedHandler;
import com.takarabako.sharetube.model.common.Role;
import com.takarabako.sharetube.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final Jwt jwt;
  private final JwtConfig jwtConfig;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;
  private final CustomAccessDeniedHandler accessDeniedHandler;
  private final CustomOAuth2SuccessHandler oAuth2SuccessHandler;
  private final CustomOAuth2FailureHandler oAuth2FailureHandler;
  private final StringRedisTemplate stringRedisTemplate;

  public SecurityConfig(Jwt jwt, JwtConfig jwtConfig, CustomOAuth2UserService customOAuth2UserService,
                        EntryPointUnauthorizedHandler entryPointUnauthorizedHandler,
                        CustomAccessDeniedHandler accessDeniedHandler, CustomOAuth2SuccessHandler oAuth2SuccessHandler,
                        CustomOAuth2FailureHandler oAuth2FailureHandler, StringRedisTemplate stringRedisTemplate) {
    this.jwt = jwt;
    this.jwtConfig = jwtConfig;
    this.customOAuth2UserService = customOAuth2UserService;
    this.entryPointUnauthorizedHandler = entryPointUnauthorizedHandler;
    this.accessDeniedHandler = accessDeniedHandler;
    this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    this.oAuth2FailureHandler = oAuth2FailureHandler;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Bean
  public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
    return new JwtAuthenticationTokenFilter(jwtConfig.getHeader(), jwt, stringRedisTemplate);
  }

  //접근 보안
  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers("/static/**","/templates/**");
  }

  @Bean
  public JwtAuthenticationProvider jwtAuthenticationProvider(Jwt jwt, UserService userService) {
    return new JwtAuthenticationProvider(jwt, userService);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Autowired
  public void configureAuthentication(AuthenticationManagerBuilder builder, @Lazy JwtAuthenticationProvider authenticationProvider) {
    // JwtAuthenticationProvider 순환 참조로 인해 @Lazy 어노테이션 추가. 임시 방편이므로 추후 자세히 조사해서 구조 파악 후 수정 요망.
    // 기존에 있던 Manager 목록에 jwt를 위한 새로운 Manager 추가
    builder.authenticationProvider(authenticationProvider);
  }

  @Bean
  public AccessDecisionManager accessDecisionManager() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

    DefaultWebSecurityExpressionHandler securityExpressionHandler = new DefaultWebSecurityExpressionHandler();
    securityExpressionHandler.setRoleHierarchy(roleHierarchy);

    WebExpressionVoter expressionVoter = new WebExpressionVoter();
    expressionVoter.setExpressionHandler(securityExpressionHandler);

    List<AccessDecisionVoter<?>> voters = Arrays.asList(expressionVoter);
    return new AffirmativeBased(voters);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.addAllowedOrigin("http://localhost:3000");
    configuration.addAllowedHeader("*");
    configuration.addAllowedHeader("Authorization");
    configuration.addAllowedMethod("*");
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    source.registerCorsConfiguration("/auth/**", configuration);
    source.registerCorsConfiguration("/login/oauth2/**", configuration);

    return source;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.
            csrf()
              .disable()
            .cors()
              .configurationSource(corsConfigurationSource())
              .and()
            .headers()
              .disable()
            .exceptionHandling()
              .authenticationEntryPoint(entryPointUnauthorizedHandler)
              .accessDeniedHandler(accessDeniedHandler)
              .and()
            .sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              .and()
            .authorizeRequests()
              .antMatchers("/","/login/**","/auth/**","/api/list/simple/**","/api/list/detail/**").permitAll()
              .antMatchers("/api/**").hasAnyAuthority("USER","ADMIN")
              .accessDecisionManager(accessDecisionManager())
              .anyRequest().authenticated()
              .and()
            .logout()
              .disable()
            .formLogin()
              .disable()
            .oauth2Login()
              .userInfoEndpoint()
              .userService(customOAuth2UserService)
              .and()
              .successHandler(oAuth2SuccessHandler)
              .failureHandler(oAuth2FailureHandler)
              .and()
            .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
  }

}
