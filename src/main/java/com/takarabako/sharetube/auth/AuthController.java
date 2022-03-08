package com.takarabako.sharetube.auth;

import com.takarabako.sharetube.auth.jwt.Jwt;
import com.takarabako.sharetube.auth.oauth2.OAuth2Result;
import com.takarabako.sharetube.controller.UserDto;
import com.takarabako.sharetube.error.UnAuthorizedException;
import com.takarabako.sharetube.model.common.ApiResult;
import com.takarabako.sharetube.model.users.User;
import com.takarabako.sharetube.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.takarabako.sharetube.model.common.ApiResult.OK;
import static com.takarabako.sharetube.model.common.ApiResult.ERROR;

@RestController
@RequestMapping(path = "/auth")
@Slf4j
public class AuthController {

  private final Jwt jwt;
  private final UserService userService;
  private final StringRedisTemplate stringRedisTemplate;

  public AuthController(Jwt jwt, UserService userService, StringRedisTemplate stringRedisTemplate) {
    this.jwt = jwt;
    this.userService = userService;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @GetMapping(path = "/login")
  public void login(HttpServletResponse response) {
    try {
      response.sendRedirect("https://sharetube-be.herokuapp.com/oauth2/authorization/google");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @GetMapping(path = "/result/{userId}")
  public ApiResult<OAuth2Result> authResult(@PathVariable(name = "userId") String userId) {

    User user = userService.findByOAuth2Id(userId);
    String accessToken = user.newAccessToken(jwt,new String[]{ user.getRole().name() });
    HashMap<String,Object> youtubeSubs = userService.getTop10Subs(userId);

    return OK(new OAuth2Result(accessToken, new UserDto(user), youtubeSubs));
  }

  @GetMapping(path = "/logout")
  public ApiResult<String> logout(HttpServletRequest request, HttpServletResponse response) {

    //로그아웃은 토큰을 토큰 만료기간과 함께 Redis에 넣어 보관 후 이후에 같은 토큰으로 접근하려 할 시에 접근 거부하는 방식으로 동작합니다.
    String token = request.getHeader("Authorization");

    //토큰이 null인 경우 로그인하지 않았음으로 간주합니다.
    if (token == null) {
      return ERROR("Your not logined before.",400);
    }
    token = URLDecoder.decode(token, StandardCharsets.UTF_8);
    String[] bearerToken = token.split(" ");
    if ("Bearer".equals(bearerToken[0])) {
      Date exp = jwt.getJwtVerifier().verify(bearerToken[1]).getExpiresAt();

      if (new Date().before(exp)) {
        try {
          stringRedisTemplate.opsForValue().set(bearerToken[1], "empty", exp.getTime(), TimeUnit.MILLISECONDS);
          log.info(stringRedisTemplate.opsForValue().get(bearerToken[1]));
        } catch (Exception e) {
          return ERROR("Error on setting token to redis",500);
        }
        return OK("successfully logouted :)");
      }

    }
    else {
      return ERROR("This token is not Bearer Token.", 400);
    }
    return ERROR("unexpected error.",500);
  }

}
