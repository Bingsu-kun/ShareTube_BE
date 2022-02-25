package com.takarabako.sharetube.auth.jwt;

import com.takarabako.sharetube.auth.oauth2.OAuth2Result;
import com.takarabako.sharetube.controller.UserDto;
import com.takarabako.sharetube.error.NotFoundException;
import com.takarabako.sharetube.model.users.User;
import com.takarabako.sharetube.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.util.ClassUtils.isAssignable;

public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final Jwt jwt;

  private final UserService usersService;

  public JwtAuthenticationProvider(Jwt jwt, UserService usersService) {
    this.jwt = jwt;
    this.usersService = usersService;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return isAssignable(JwtAuthenticationToken.class,authentication);
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
    String authRequest = authenticationToken.authenticationRequest();
    try {
      // 내부 비즈니스 로직을 통해 유저가 존재하는지 확인.
      User user = usersService.findByOAuth2Id(authRequest);
      // 반환 할 JwtAuthenticationToken을 만들어준다.
      JwtAuthenticationToken authenticatedToken =
              new JwtAuthenticationToken(new JwtAuthentication(user.getOAuth2Id(),user.getEmail()),null,createAuthorityList(user.getRole().name()));
      String accessToken = user.newAccessToken(jwt, new String[]{ user.getRole().name() });
      authenticatedToken.setDetails(new OAuth2Result(accessToken,new UserDto(user)));
      return authenticatedToken;
    } catch (NotFoundException e) {
      throw new UsernameNotFoundException(e.getMessage());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e.getMessage());
    } catch (DataAccessException e) {
      throw new AuthenticationServiceException(e.getMessage());
    }
  }
}

