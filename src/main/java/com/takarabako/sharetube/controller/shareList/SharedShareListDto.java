package com.takarabako.sharetube.controller.shareList;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class SharedShareListDto {

  private Integer totalSharedListCount;

  private List<ShareListResponseDto> sharedLists;

  public SharedShareListDto(List<ShareListResponseDto> lists) {
    this.totalSharedListCount = lists.size();
    this.sharedLists = lists;
  }

}
