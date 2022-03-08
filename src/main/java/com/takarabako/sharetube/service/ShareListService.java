package com.takarabako.sharetube.service;

import com.takarabako.sharetube.error.UnAuthorizedException;
import com.takarabako.sharetube.model.lists.ShareList;
import com.takarabako.sharetube.model.users.User;
import com.takarabako.sharetube.model.youtube.ChannelDto;
import com.takarabako.sharetube.model.youtube.YoutubeChannelDto;
import com.takarabako.sharetube.repository.ShareListRepository;
import com.takarabako.sharetube.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ShareListService {

  private final ShareListRepository shareListRepository;
  private final UserRepository userRepository;

  public ShareListService(ShareListRepository shareListRepository, UserRepository userRepository) {
    this.shareListRepository = shareListRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public ShareList create(String userId, String listTitle, List<ChannelDto> channels) {
    User author = userRepository.getById(userId);
    String channelIds = createIdString(channels);

    ShareList shareList = new ShareList(author,listTitle,channelIds);
    shareListRepository.save(shareList);

    return shareList;
  }

  @Transactional
  public ShareList getList(UUID id) {
    ShareList list = shareListRepository.getById(id);
    list.addViews();

    // TODO - 채널 저자 id, 저자 닉네임, 타이틀, 구독자, 채널목록(ChannelDto), 조회수를 Dto로 만들어야함.

    return list;
  }

  @Transactional
  public ShareList update(String oAuth2Id, UUID id, String listTitle, List<ChannelDto> channels) {

    ShareList list = shareListRepository.getById(id);

    if (!oAuth2Id.equals(list.getAuthor().getOAuth2Id()))
      throw new UnAuthorizedException("접속한 유저와 list의 저자가 다릅니다.");

    list.setTitle(listTitle);
    list.setChannelIds(createIdString(channels));
    shareListRepository.save(list);

    return list;
  }

  @Transactional
  public boolean delete(String oAuth2Id, UUID id) {

    ShareList list = shareListRepository.getById(id);

    if (!oAuth2Id.equals(list.getAuthor().getOAuth2Id()))
      throw new UnAuthorizedException("접속한 유저와 list의 저자가 다릅니다.");

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

    if (list.getSubscriber().contains(user))
      throw new IllegalArgumentException("이미 구독 중입니다.");
    else {
      list.addSubscriber(user);
      shareListRepository.save(list);
    }

    return true;
  }

  @Transactional
  public boolean unsubscribe(String oAuth2Id, UUID id) {

    ShareList list = shareListRepository.getById(id);
    User user = userRepository.getById(oAuth2Id);

    if (!list.getSubscriber().contains(user))
      throw new IllegalArgumentException("구독 목록에 존재하지 않습니다.");
    else {
      list.removeSubscriber(user);
      shareListRepository.save(list);
    }

    return true;
  }

  private String createIdString(List<ChannelDto> channels) {

    StringBuilder channelIds = new StringBuilder();

    for (ChannelDto channel : channels) {
      if (channelIds.toString().equals("")) {
        channelIds.append(channel.getId());
      } else {
        channelIds.append(",").append(channel.getId());
      }
    }

    return channelIds.toString();
  }

}
