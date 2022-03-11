package com.takarabako.sharetube.controller.shareList;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ShareListRequestDto {

  private String thumbnail;
  private String title;
  private String description;
  private List<String> channels;

}
