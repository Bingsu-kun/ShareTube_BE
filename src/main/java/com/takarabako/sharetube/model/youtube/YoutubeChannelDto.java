package com.takarabako.sharetube.model.youtube;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class YoutubeChannelDto {

  private String id;
  private YoutubeSnippetDto snippet;
  private YoutubeStatisticsDto statistics;

}
