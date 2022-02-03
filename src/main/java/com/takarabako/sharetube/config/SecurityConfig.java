package com.takarabako.sharetube.config;

import com.takarabako.sharetube.auth.CustomAccessDeniedHandler;
import com.takarabako.sharetube.auth.oauth2.CustomOAuth2FailureHandler;
import com.takarabako.sharetube.auth.oauth2.CustomOAuth2UserService;
import com.takarabako.sharetube.auth.EntryPointUnauthorizedHandler;
import com.takarabako.sharetube.model.common.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private CustomOAuth2UserService customOAuth2UserService;
  private EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;
  private CustomAccessDeniedHandler accessDeniedHandler;
  private CustomOAuth2FailureHandler oAuth2FailureHandler;

  public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, EntryPointUnauthorizedHandler entryPointUnauthorizedHandler,
                        CustomAccessDeniedHandler accessDeniedHandler,
                        CustomOAuth2FailureHandler oAuth2FailureHandler)
  {
    this.customOAuth2UserService = customOAuth2UserService;
    this.accessDeniedHandler = accessDeniedHandler;
    this.entryPointUnauthorizedHandler = entryPointUnauthorizedHandler;
    this.oAuth2FailureHandler = oAuth2FailureHandler;
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

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.
            csrf()
              .disable()
            .headers()
              .disable()
//            .exceptionHandling()
//              .authenticationEntryPoint(entryPointUnauthorizedHandler)
//              .accessDeniedHandler(accessDeniedHandler)
//              .and()
            .sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              .and()
            .authorizeRequests()
              .antMatchers("/","/login/**","/oauth2/**").permitAll()
              .antMatchers("/list").hasAnyRole(Role.USER.name(),Role.ADMIN.name())
              .accessDecisionManager(accessDecisionManager())
              .anyRequest().authenticated()
              .and()
            .logout()
              .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
              .and()
            .formLogin()
              .disable()
            .oauth2Login()
              .userInfoEndpoint()
              .userService(customOAuth2UserService)
              .and()
              .failureHandler(oAuth2FailureHandler);
  }

}
