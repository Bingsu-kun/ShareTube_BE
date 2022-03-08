package com.takarabako.sharetube.model.youtube;

import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.HashMap;

@Getter
@ToString
public class YoutubeSnippetDto {

  private Date publishedAt;
  private String title;
  private String description;
  private String customUrl;
  private YoutubeResourceIdDto resourceId;
  private String channelId;
  private HashMap<String, Object> thumbnails;

}
