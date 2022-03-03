package com.takarabako.sharetube.model.youtube;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class YoutubeChannelDto {

  private String kind;
  private String etag;
  private String id;
  private YoutubeSnippetDto snippet;
  private YoutubeContentDetailsDto  contentDetails;

}
