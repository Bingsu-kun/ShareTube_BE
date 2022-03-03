package com.takarabako.sharetube.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takarabako.sharetube.error.JsonParseException;
import com.takarabako.sharetube.model.youtube.YoutubeChannelDto;
import com.takarabako.sharetube.model.youtube.YoutubeSubscriptionResponseDto;
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
  private HttpHeaders headers = new HttpHeaders();
  private YoutubeSubscriptionResponseDto responseBody;

  public YoutubeUtils(RestTemplate rt, ObjectMapper om) {
    this.rt = rt;
    this.om = om;
    this.om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public HashMap<String, Object> getSubscriptions(String userId, String accessToken) {

    String nextPageToken;
    int total;
    int repeatCount;

    // 채널 반환용 HashMap 객체
    List<YoutubeChannelDto> items = new ArrayList<YoutubeChannelDto>();

    // HttpHeader 객체 생성 후 토큰 정보 넣기
    headers.add("Authorization","Bearer "+accessToken);

    // HttpEntity 만들어서 HttpHeader 객체 추가 (Body는 불필요하므로 생략. 필요시 추가 가능)
    HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(headers);

    // 요청 URL
    String requestUrl = "https://www.googleapis.com/youtube/v3/subscriptions?" +
            "part=snippet,contentDetails&mine=true&maxResults=50&key="+apiKey;

    try {
      responseBody = om.readValue(rt.exchange(requestUrl, HttpMethod.GET, entity, String.class).getBody(), YoutubeSubscriptionResponseDto.class);
      nextPageToken = responseBody.getNextPageToken();
      total = responseBody.getPageInfo().getTotalResults();
      repeatCount = total % 50 > 0 ? total / 50 : total / 50 - 1;
      items.addAll(responseBody.getItems());
      requestUrl = "https://www.googleapis.com/youtube/v3/subscriptions?" +
              "part=snippet,contentDetails&mine=true&maxResults=50&pageToken="+nextPageToken+"&key="+apiKey;

      for(int counter = 0; counter < repeatCount; counter++) {
        responseBody = om.readValue(rt.exchange(requestUrl, HttpMethod.GET, entity, String.class).getBody(), YoutubeSubscriptionResponseDto.class);
        nextPageToken = responseBody.getNextPageToken();
        items.addAll(responseBody.getItems());
        requestUrl = "https://www.googleapis.com/youtube/v3/subscriptions?" +
                "part=snippet,contentDetails&mine=true&maxResults=50&pageToken="+nextPageToken+"&key="+apiKey;
      }

    } catch (JsonProcessingException e) {
      throw new JsonParseException("json 데이터를 파싱하는 중 에러가 발생했습니다. cause : " + e.getMessage());
    }

    HashMap<String, Object> response = new HashMap<String, Object>();

    response.put("totalChannels",total);
    response.put("channelDetails",items);

    return response;
  }

}
