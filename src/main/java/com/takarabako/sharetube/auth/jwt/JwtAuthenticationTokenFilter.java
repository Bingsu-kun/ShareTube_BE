package com.takarabako.sharetube.auth.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.takarabako.sharetube.error.UnAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Slf4j
public class JwtAuthenticationTokenFilter extends GenericFilterBean {

  private static final Pattern BEARER = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);

  private final String headerKey;

  private final Jwt jwt;

  private final StringRedisTemplate stringRedisTemplate;

  public JwtAuthenticationTokenFilter(String headerKey, Jwt jwt, StringRedisTemplate stringRedisTemplate) {
    this.headerKey = headerKey;
    this.jwt = jwt;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    // SecurityContext에 인증정보가 없음
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      // header에서 토큰 가져오기
      String accessToken = getAuthToken(request);

      //토큰이 존재
      if (accessToken != null) {
        //이미 로그아웃 처리 되었던 토큰인지 확인
        String storedToken = stringRedisTemplate.opsForValue().get(accessToken);
        if (storedToken != null)
          throw new UnAuthorizedException("You already logouted before. This token is not available. So re-login please.");

        try {
          Jwt.Claims claims = new Jwt.Claims(jwt.getJwtVerifier().verify(accessToken));
          //만료되지 않은 토큰은 리프레쉬
          if (new Date().before(claims.exp)) {
            String refreshToken = jwt.refreshToken(accessToken);
            response.setHeader(headerKey,refreshToken);
          }


          String id = claims.id;
          String email = claims.email;

          List<GrantedAuthority> authorities = claims.roles == null || claims.roles.length == 0 ?
                  Collections.emptyList() :
                  Arrays.stream(claims.roles).map(SimpleGrantedAuthority::new).collect(toList());

          if (id != null && email != null && authorities.size() > 0) {
            JwtAuthenticationToken authenticationToken =
                    new JwtAuthenticationToken(new JwtAuthentication(id,email),null,authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          }

        } catch (TokenExpiredException e) {
          throw new TokenExpiredException(e.getMessage());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    // 인증 정보가 있는 경우는 추가 동작 없음.
    filterChain.doFilter(request,response);
  }

  private String getAuthToken(HttpServletRequest request) {
    String token = request.getHeader(headerKey);
    if (token != null) {
      token = URLDecoder.decode(token, StandardCharsets.UTF_8);
      String[] parts = token.split(" ");
      if (parts.length == 2) {
        String scheme = parts[0];
        String credentials = parts[1];
        // token이 Bearer로 시작한다면 토큰 값을 추출
        return BEARER.matcher(scheme).matches() ? credentials : null;
      }
    }
    return null;
  }
}
