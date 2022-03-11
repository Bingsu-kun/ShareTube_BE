package com.takarabako.sharetube.controller.user;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.takarabako.sharetube.auth.jwt.JwtAuthentication;
import com.takarabako.sharetube.controller.shareList.ShareListResponseDto;
import com.takarabako.sharetube.error.NotFoundException;
import com.takarabako.sharetube.error.UnAuthorizedException;
import com.takarabako.sharetube.model.common.ApiResult;
import com.takarabako.sharetube.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

import static com.takarabako.sharetube.model.common.ApiResult.ERROR;
import static com.takarabako.sharetube.model.common.ApiResult.OK;

@RestController
@RequestMapping(path = "/api")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping(path = "/me")
  public ApiResult<UserDto> me(@AuthenticationPrincipal JwtAuthentication authentication) {
    try {
      return OK (new UserDto(userService.findByOAuth2Id(authentication.userId)));
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR("unexpected error occurred", 500);
    }
  }

  @GetMapping(path = "/rank")
  public ApiResult<String> rank(@AuthenticationPrincipal JwtAuthentication authentication) {
    try {
      return OK(userService.rankPercent(authentication.userId));
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR("unexpected error occurred", 500);
    }
  }

  @GetMapping(path = "/summary")
  public ApiResult<ActivitySummaryDto> summary(@AuthenticationPrincipal JwtAuthentication authentication) {

    int[] summary = userService.summary(authentication.userId);
    try {
      return OK(new ActivitySummaryDto(summary));
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR("unexpected error occurred", 500);
    }
  }

  @GetMapping(path = "/list/created")
  public ApiResult<List<ShareListResponseDto>> getMyListSummary(@AuthenticationPrincipal JwtAuthentication authentication) {
    try {
      return OK(userService.getMyList(authentication.userId));
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR("unexpected error occurred", 500);
    }
  }

  @GetMapping(path = "/list/shared")
  public ApiResult<List<ShareListResponseDto>> getSharedList(@AuthenticationPrincipal JwtAuthentication authentication) {
    try {
      return OK(userService.getSharedList(authentication.userId));
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR("unexpected error occurred", 500);
    }
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

  @GetMapping(path = "/subscriptions/top")
  public ApiResult<HashMap<String,Object>> getTop10Subs(@AuthenticationPrincipal JwtAuthentication authentication) {
    try {
      return OK( userService.getTop10Subs(authentication.userId));
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
