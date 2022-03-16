package com.takarabako.sharetube.controller.shareList;

import lombok.Getter;
import lombok.ToString;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Getter
@ToString
public class CreatedShareListDto {

  private Integer totalCreatedListCount;

  private List<ShareListResponseDto> createdList;

  public CreatedShareListDto(List<ShareListResponseDto> lists){
    this.totalCreatedListCount = lists.size();
    this.createdList = lists;
  }

}
