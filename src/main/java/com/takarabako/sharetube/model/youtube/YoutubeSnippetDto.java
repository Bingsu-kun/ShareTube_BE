package com.takarabako.sharetube.model.youtube;

import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Getter
@ToString
public class YoutubeSnippetDto {

  private Date publishedAt;
  private String title;
  private String description;
  private Object resourceId;
  private String channelId;
  private Object thumbnails;

}
