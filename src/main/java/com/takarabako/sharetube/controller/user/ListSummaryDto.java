package com.takarabako.sharetube.controller.user;

import com.takarabako.sharetube.controller.shareList.ShareListResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class ListSummaryDto {

  public final int totalLists;
  public final List<ShareListResponseDto> lists;

}
