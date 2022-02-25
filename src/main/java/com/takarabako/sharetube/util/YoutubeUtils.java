package com.takarabako.sharetube.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
public class YoutubeUtils {

  @Value("${youtube.key}")
  private String apiKey;

  private RestTemplate rt;
  private ObjectMapper om;
  private HttpHeaders headers = new HttpHeaders();
  private HashMap<String,Object> responseBody;

  public YoutubeUtils(RestTemplate rt, ObjectMapper om) {
    this.rt = rt;
    this.om = om;
  }

  public HashMap<String, Object> getSubscriptions(String userId, String accessToken) {
    // HttpHeader 객체 생성 후 토큰 정보 넣기
    headers.add("Authorization","Bearer "+accessToken);

    // HttpEntity 만들어서 HttpHeader 객체 추가 (Body는 불필요하므로 생략. 필요시 추가 가능)
    HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(headers);

    // 요청 URL
    String requestUrl = "https://www.googleapis.com/youtube/v3/subscriptions?" +
            "part=snippet,contentDetails&mine=true&maxResults=50&key="+apiKey;

    try {
      responseBody = om.readValue(rt.exchange(requestUrl, HttpMethod.GET, entity, String.class).getBody(), HashMap.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return responseBody;
  }

}
