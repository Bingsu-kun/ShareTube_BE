package com.takarabako.sharetube.controller.user;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ActivitySummaryDto {

  private final int totalMyLists;
  private final int totalViews;

  public ActivitySummaryDto(int[] summary) {
    this.totalMyLists = summary[0];
    this.totalViews = summary[1];
  }

}
