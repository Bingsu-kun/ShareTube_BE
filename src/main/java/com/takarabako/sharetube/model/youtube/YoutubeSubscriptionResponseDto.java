package com.takarabako.sharetube.model.youtube;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

@Getter
@ToString
public class YoutubeSubscriptionResponseDto {

  private String kind;
  private String etag;
  private String nextPageToken;
  private String prevPageToken;
  private YoutubePageInfoDto pageInfo;
  private List<YoutubeChannelDto> items;

}
