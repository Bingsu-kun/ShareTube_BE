package com.takarabako.sharetube.service;

import com.takarabako.sharetube.controller.shareList.ShareListResponseDto;
import com.takarabako.sharetube.error.UnAuthorizedException;
import com.takarabako.sharetube.model.lists.ShareList;
import com.takarabako.sharetube.model.users.User;
import com.takarabako.sharetube.model.youtube.ChannelDto;
import com.takarabako.sharetube.repository.ShareListRepository;
import com.takarabako.sharetube.repository.UserRepository;
import com.takarabako.sharetube.util.YoutubeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ShareListService {

  private final ShareListRepository shareListRepository;
  private final UserRepository userRepository;
  private final YoutubeUtils youtubeUtils;

  public ShareListService(ShareListRepository shareListRepository, UserRepository userRepository, YoutubeUtils youtubeUtils) {
    this.shareListRepository = shareListRepository;
    this.userRepository = userRepository;
    this.youtubeUtils = youtubeUtils;
  }

  @Transactional
  public ShareList create(String userId, String thumbnail, String listTitle, String description, List<String> channels) {
    User author = userRepository.getById(userId);
    String channelIds = createIdString(channels);
    ShareList shareList = new ShareList(author,thumbnail,listTitle,description,channelIds);
    shareListRepository.save(shareList);

    return shareList;
  }

  @Transactional
  public ShareListResponseDto getSimpleList(UUID id) {
    ShareList list = shareListRepository.getById(id);
    int channels = list.getChannelIds().split(",").length;

    return ShareListResponseDto.SIMPLE(list.getId(),list.getAuthor().getOAuth2Id(),list.getAuthor().getNickname(),
            list.getTitle(),list.getThumbnail(),list.getViews(),channels);
  }

  @Transactional
  public ShareListResponseDto getDetailList(String oauth2Id, UUID id) {
    // ??????????????? oauth2Id ??? ?????? ????????? ???????????? ???????????? ??????. ??????????????? ????????? ???????????? ???????????? ???????????? ??????.
    ShareList list = shareListRepository.getById(id);
    if (!list.getAuthor().getOAuth2Id().equals(oauth2Id))
      list.addViews();
    log.info("when getChannel : " + list.getChannelIds());
    // 50?????? ????????? ???????????? ??????
    String[] channels = list.getChannelIds().split(",");
    StringBuilder channelIds = new StringBuilder();

    List<ChannelDto> channelList = new ArrayList<>();

    for (int count = 0; count < channels.length; count++) {
      // 50??? ?????? ??????.
      if (channelIds.toString().equals("")) {
        channelIds.append(channels[count]);
      } else {
        channelIds.append(",").append(channels[count]);
      }

      // 50??? ????????? ??? ?????? ?????? ?????????.
      if ((count+1) % 50 == 0) {
        channelList.addAll(youtubeUtils.getChannelsDetails(channelIds.toString()));
        channelIds = new StringBuilder();
      }
    }

    if (!channelIds.toString().equals("")) {
      channelList.addAll(youtubeUtils.getChannelsDetails(channelIds.toString()));
    }

    return ShareListResponseDto.DETAIL(list.getId(),list.getAuthor().getOAuth2Id(),list.getAuthor().getNickname(),
            list.getTitle(),list.getThumbnail(),list.getViews(),channelList);
  }

  @Transactional
  public ShareList update(String oAuth2Id, UUID id, String thumbnail, String listTitle, String description, List<String> channels) {

    ShareList list = shareListRepository.getById(id);

    if (!oAuth2Id.equals(list.getAuthor().getOAuth2Id()))
      throw new UnAuthorizedException("????????? ????????? list??? ????????? ????????????.");

    list.setThumbnail(thumbnail);
    list.setTitle(listTitle);
    list.setChannelIds(createIdString(channels));
    list.setDescription(description);
    shareListRepository.save(list);

    return list;
  }

  @Transactional
  public boolean delete(String oAuth2Id, UUID id) {

    ShareList list = shareListRepository.getById(id);

    if (!oAuth2Id.equals(list.getAuthor().getOAuth2Id()))
      throw new UnAuthorizedException("????????? ????????? list??? ????????? ????????????.");

    try {
      shareListRepository.delete(list);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @Transactional
  public boolean subscribe(String oAuth2Id, UUID id) {

    ShareList list = shareListRepository.getById(id);
    User user = userRepository.getById(oAuth2Id);

    if (user.getSubscribing().contains(list))
      throw new IllegalArgumentException("?????? ?????? ????????????.");
    else {
      user.addSubList(list);
      list.addSubscriber(user);
      userRepository.save(user);
      shareListRepository.save(list);
    }

    return true;
  }

  @Transactional
  public boolean unsubscribe(String oAuth2Id, UUID id) {

    ShareList list = shareListRepository.getById(id);
    User user = userRepository.getById(oAuth2Id);

    if (!user.getSubscribing().contains(list))
      throw new IllegalArgumentException("?????? ????????? ???????????? ????????????.");
    else {
      user.removeSubList(list);
      list.removeSubscriber(user);
      userRepository.save(user);
      shareListRepository.save(list);
    }

    return true;
  }

  private String createIdString(List<String> channels) {

    StringBuilder channelIds = new StringBuilder();

    for (String channel : channels) {
      if (channelIds.toString().equals("")) {
        channelIds.append(channel);
      } else {
        channelIds.append(",").append(channel);
      }
    }

    return channelIds.toString();
  }

}
