package com.takarabako.sharetube.model.youtube;

import lombok.*;

import java.util.HashMap;

@Getter
@ToString
@AllArgsConstructor
public class ChannelDto {

  private final String id;

  private final String title;

  private final String description;

  private final String channelUrl;

  private final HashMap<String, Object> thumbnail;

  private final int subscribers;

}
