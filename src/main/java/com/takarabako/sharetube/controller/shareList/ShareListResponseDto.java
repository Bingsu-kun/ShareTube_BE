package com.takarabako.sharetube.controller.shareList;

import com.takarabako.sharetube.model.youtube.ChannelDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
public class ShareListResponseDto {

  private UUID id;
  private String authorId;
  private String authorNickName;
  private String title;
  private String thumbnail;
  private int views;
  private Object channels;

  public ShareListResponseDto(UUID id, String authorId, String authorNickName, String title, String thumbnail, int views, Object channels) {
    this.id = id;
    this.authorId = authorId;
    this.authorNickName = authorNickName;
    this.title = title;
    this.thumbnail = thumbnail;
    this.views = views;
    this.channels = channels;
  }

  public static ShareListResponseDto SIMPLE(UUID id, String authorId, String authorNickName, String title, String thumbnail, int views, int channels) {
    return new ShareListResponseDto(id,authorId,authorNickName,title,thumbnail,views,channels);
  }

  public static ShareListResponseDto DETAIL(UUID id, String authorId, String authorNickName, String title, String thumbnail, int views, List<ChannelDto> channels){
    return new ShareListResponseDto(id,authorId,authorNickName,title,thumbnail,views,channels);
  }

}
