package com.takarabako.sharetube.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takarabako.sharetube.error.AccessTokenExpiredException;
import com.takarabako.sharetube.error.JsonParseException;
import com.takarabako.sharetube.model.youtube.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class YoutubeUtils {

  @Value("${youtube.key}")
  private String apiKey;

  private RestTemplate rt;
  private ObjectMapper om;
  private YoutubeSubscriptionResponseDto responseBody;

  public YoutubeUtils(RestTemplate rt, ObjectMapper om) {
    this.rt = rt;
    this.om = om;
    this.om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public HashMap<String, Object> getSubscriptions(String accessToken) {

    String nextPageToken;
    int total;
    int repeatCount;

    // 채널 반환용 HashMap 객체
    List<ChannelDto> items = new ArrayList<>();

    // HttpHeader 객체 생성 후 토큰 정보 넣기
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization","Bearer "+accessToken);

    // HttpEntity 만들어서 HttpHeader 객체 추가 (Body는 불필요하므로 생략. 필요시 추가 가능)
    HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(headers);

    // 요청 URL
    String requestUrl = "https://www.googleapis.com/youtube/v3/subscriptions?" +
            "part=snippet&mine=true&maxResults=50&key="+apiKey;



    try {
      responseBody = om.readValue(rt.exchange(requestUrl, HttpMethod.GET, entity, String.class).getBody(), YoutubeSubscriptionResponseDto.class);
      nextPageToken = responseBody.getNextPageToken();
      total = responseBody.getPageInfo().getTotalResults();
      repeatCount = total % 50 > 0 ? total / 50 : total / 50 - 1;

      // 구독 채널 개수가 1000 개 이상일 경우 1000개 제한.
      if (repeatCount > 19 )
        repeatCount = 19;

      String id = createIdString(responseBody.getItems());
      items.addAll(getChannelsDetails(id));

      requestUrl = "https://www.googleapis.com/youtube/v3/subscriptions?" +
              "part=snippet&mine=true&maxResults=50&pageToken="+nextPageToken+"&key="+apiKey;

      for(int counter = 0; counter < repeatCount; counter++) {
        responseBody = om.readValue(rt.exchange(requestUrl, HttpMethod.GET, entity, String.class).getBody(), YoutubeSubscriptionResponseDto.class);
        nextPageToken = responseBody.getNextPageToken();
        id = createIdString(responseBody.getItems());
        items.addAll(getChannelsDetails(id));
        requestUrl = "https://www.googleapis.com/youtube/v3/subscriptions?" +
                "part=snippet&mine=true&maxResults=50&pageToken="+nextPageToken+"&key="+apiKey;
      }

    } catch (JsonProcessingException e) {
      throw new JsonParseException("json 데이터를 파싱하는 중 에러가 발생했습니다. cause : " + e.getMessage());
    } catch (Exception e) {
      throw new AccessTokenExpiredException("재 로그인이 필요합니다.");
    }

    HashMap<String, Object> response = new HashMap<>();

    response.put("totalSubscriptions",total);
    response.put("channelDetails",items);

    return response;
  }

  public HashMap<String, Object> getTop10Subs(String accessToken) {

    int total;

    List<ChannelDto> items = new ArrayList<>();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization","Bearer "+accessToken);

    HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(headers);

    String requestUrl = "https://www.googleapis.com/youtube/v3/subscriptions?" +
            "part=snippet&mine=true&maxResults=10&key="+apiKey;

    try {
      responseBody = om.readValue(rt.exchange(requestUrl, HttpMethod.GET, entity, String.class).getBody(), YoutubeSubscriptionResponseDto.class);
      String id = createIdString(responseBody.getItems());
      total = responseBody.getPageInfo().getTotalResults();
      items.addAll(getChannelsDetails(id));
    } catch (JsonProcessingException e) {
      throw new JsonParseException("json 데이터를 파싱하는 중 에러가 발생했습니다. cause : " + e.getMessage());
    } catch (Exception e) {
      throw new AccessTokenExpiredException("재 로그인이 필요합니다.");
    }

    HashMap<String, Object> response = new HashMap<>();

    response.put("totalSubscriptions",total);
    response.put("topChannels",items);

    return response;
  }

  public List<ChannelDto> getChannelsDetails(String channelIds) {
    List<ChannelDto> channels = new ArrayList<>();

    HttpHeaders headers = new HttpHeaders();

    HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(headers);

    String requestUrl = "https://www.googleapis.com/youtube/v3/channels?part=snippet,statistics&maxResults=50&id="
            + channelIds + "&key=" + apiKey;

    try {
      responseBody = om.readValue(rt.exchange(requestUrl, HttpMethod.GET, entity, String.class).getBody(), YoutubeSubscriptionResponseDto.class);
      responseBody.getItems().forEach((youtubeChannelDto) -> {
        YoutubeSnippetDto snippet = youtubeChannelDto.getSnippet();
        YoutubeStatisticsDto statistics = youtubeChannelDto.getStatistics();
        String link;
        if (snippet.getCustomUrl() == null)
          link = "https://youtube.com/channel/" + youtubeChannelDto.getId();
        else
          link = "https://youtube.com/" + snippet.getCustomUrl();
        ChannelDto channel = new ChannelDto(youtubeChannelDto.getId(),snippet.getTitle(),snippet.getDescription(),
                link, snippet.getThumbnails(), statistics.getSubscriberCount());

        channels.add(channel);
      });
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return channels;
  }

  private String createIdString(List<YoutubeChannelDto> channels) {

    StringBuilder id = new StringBuilder();

    for (YoutubeChannelDto channel : channels) {
      if (id.toString().equals("")) {
        id.append(channel.getSnippet().getResourceId().getChannelId());
      } else {
        id.append(",").append(channel.getSnippet().getResourceId().getChannelId());
      }
    }

    return id.toString();
  }

}
