package com.takarabako.sharetube.controller.shareList;

import com.takarabako.sharetube.auth.jwt.JwtAuthentication;
import com.takarabako.sharetube.error.NotFoundException;
import com.takarabako.sharetube.error.UnAuthorizedException;
import com.takarabako.sharetube.model.common.ApiResult;
import com.takarabako.sharetube.model.lists.ShareList;
import com.takarabako.sharetube.service.ShareListService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.takarabako.sharetube.model.common.ApiResult.OK;
import static com.takarabako.sharetube.model.common.ApiResult.ERROR;
import static com.takarabako.sharetube.controller.shareList.ShareListResponseDto.SIMPLE;
import static com.takarabako.sharetube.controller.shareList.ShareListResponseDto.DETAIL;

@RestController
@RequestMapping(path = "/api")
public class ShareListController {

  private final ShareListService shareListService;

  public ShareListController(ShareListService shareListService) {
    this.shareListService = shareListService;
  }

  @GetMapping(path = "/list/simple/{listId}")
  public ApiResult<ShareListResponseDto> getSimpleList(@PathVariable(value = "listId") UUID id) {
    try {
      return OK(shareListService.getSimpleList(id));
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR("unexpected error occurred", 500);
    }
  }

  @GetMapping(path = "/list/detail/{listId}")
  public ApiResult<ShareListResponseDto> getDetailList(@AuthenticationPrincipal JwtAuthentication authentication, @PathVariable(value = "listId") UUID id) {
    try {
      return OK(shareListService.getDetailList(authentication.userId, id));
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR("unexpected error occurred", 500);
    }
  }

  @PostMapping(path = "/list")
  public ApiResult<ShareListResponseDto> createList(@AuthenticationPrincipal JwtAuthentication authentication, @RequestBody ShareListRequestDto shareListRequestDto) {
    ShareList list = shareListService.create(authentication.userId, shareListRequestDto.getThumbnail(),
            shareListRequestDto.getTitle(), shareListRequestDto.getDescription(), shareListRequestDto.getChannels());
    int channels = list.getChannelIds().split(",").length;
    try {
      return OK(SIMPLE(list.getId(),list.getAuthor().getOAuth2Id(),list.getAuthor().getNickname(),list.getTitle(),
              /*list.getThumbnail()*/"test",list.getViews(),channels));
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR("unexpected error occurred", 500);
    }
  }

  @PutMapping(path = "/list/{listId}")
  public ApiResult<ShareListResponseDto> updateList(@AuthenticationPrincipal JwtAuthentication authentication,
                                                    @PathVariable(value = "listId") UUID id,
                                                    @RequestBody ShareListRequestDto shareListRequestDto) {
    ShareList list = shareListService.update(authentication.userId, id, shareListRequestDto.getThumbnail(),
            shareListRequestDto.getTitle(), shareListRequestDto.getDescription(), shareListRequestDto.getChannels());
    int channels = list.getChannelIds().split(",").length;
    try {
      return OK(SIMPLE(list.getId(),list.getAuthor().getOAuth2Id(),list.getAuthor().getNickname(),list.getTitle(),
              /*list.getThumbnail()*/"test",list.getViews(),channels));
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR("unexpected error occurred", 500);
    }
  }

  @DeleteMapping(path = "/list/{listId}")
  public ApiResult<Boolean> deleteList(@AuthenticationPrincipal JwtAuthentication authentication,
                                       @PathVariable(value = "listId") UUID id) {
    try {
      return OK(shareListService.delete(authentication.userId, id));
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR("unexpected error occurred", 500);
    }
  }

  @PostMapping(path = "/list/sub/{listId}")
  public ApiResult<Boolean> subscribe(@AuthenticationPrincipal JwtAuthentication authentication,
                                      @PathVariable(value = "listId") UUID id) {
    try {
      return OK(shareListService.subscribe(authentication.userId, id));
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (IllegalArgumentException e) {
      return ERROR("이미 구독 중 입니다.", 400);
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR("unexpected error occurred", 500);
    }

  }

  @DeleteMapping(path = "/list/sub/{listId}")
  public ApiResult<Boolean> unsubscribe(@AuthenticationPrincipal JwtAuthentication authentication,
                                      @PathVariable(value = "listId") UUID id) {
    try {
      return OK(shareListService.unsubscribe(authentication.userId, id));
    } catch (NotFoundException e) {
      return ERROR(new NotFoundException(e.getMessage()),404);
    } catch (UnAuthorizedException e) {
      return ERROR(new UnAuthorizedException(e.getMessage()), 401);
    } catch (IllegalArgumentException e) {
      return ERROR("이미 구독 중 입니다.", 400);
    } catch (Exception e) {
      e.printStackTrace();
      return ERROR("unexpected error occurred", 500);
    }

  }

}
