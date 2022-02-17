package com.takarabako.sharetube.controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.takarabako.sharetube.auth.jwt.JwtAuthentication;
import com.takarabako.sharetube.error.NotFoundException;
import com.takarabako.sharetube.error.UnAuthorizedException;
import com.takarabako.sharetube.model.common.ApiResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.takarabako.sharetube.model.common.ApiResult.OK;
import static com.takarabako.sharetube.model.common.ApiResult.ERROR;

@RestController
@RequestMapping(path = "/api")
public class UserController {

  @GetMapping(path = "/hcheck")
  public ApiResult<String> hcheck() {
    return OK("ShareTube_BE server is Running!");
  }

  @GetMapping(path = "/authcheck")
  public ApiResult<String> authcheck(@AuthenticationPrincipal JwtAuthentication authentication) {
    try {
      return OK("auth pass :)" + authentication.userId);
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
