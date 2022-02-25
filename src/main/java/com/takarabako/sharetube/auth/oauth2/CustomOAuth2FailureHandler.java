package com.takarabako.sharetube.auth.oauth2;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomOAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setHeader("content-type", "application/json");
    response.getWriter().write("{\"success\" : \"false\", \"response\" : null, \"error\" : {\"message\" : \"Authentication error (cause: OAuth2 authentication failed)" + exception + "\", \"code\" : 401}}");
    response.getWriter().flush();
    response.getWriter().close();

    // 인증에 실패했을 경우 위와 같은 응답. 클라이언트에서 실패를 알리고 다시 리다이렉트 시켜줘야 함.

  }
}
