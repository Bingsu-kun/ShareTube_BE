package com.takarabako.sharetube.controller.shareList;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class BothShareListDto {

  private Integer totalCreatedListCount;

  private List<ShareListResponseDto> createdList;

  private Integer totalSharedListCount;

  private List<ShareListResponseDto> sharedLists;

  public BothShareListDto(List<ShareListResponseDto> createdList, List<ShareListResponseDto> sharedList) {
    this.totalCreatedListCount = createdList.size();
    this.createdList = createdList;
    this.totalSharedListCount = sharedList.size();
    this.sharedLists = sharedList;
  }

}
