package com.takarabako.sharetube.controller.user;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.takarabako.sharetube.auth.jwt.JwtAuthentication;
import com.takarabako.sharetube.error.NotFoundException;
import com.takarabako.sharetube.error.UnAuthorizedException;
import com.takarabako.sharetube.model.common.ApiResult;
import com.takarabako.sharetube.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import static com.takarabako.sharetube.model.common.ApiResult.OK;
import static com.takarabako.sharetube.model.common.ApiResult.ERROR;

@RestController
@RequestMapping(path = "/api")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping(path = "/subscriptions")
  public ApiResult<HashMap<String,Object>> getSubscriptions(@AuthenticationPrincipal JwtAuthentication authentication) {
    try {
      return OK( userService.getSubscriptions(authentication.userId) );
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (NullPointerException e) {
      return ERROR("Token is not Exists.", 400);
    } catch (JWTDecodeException e) {
      return ERROR("It's not Jwt Token.", 400);
    }
  }

}
