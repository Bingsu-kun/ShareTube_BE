package com.takarabako.sharetube.model.common;

import lombok.Getter;

@Getter
public class ApiResult<T> {

  private final boolean success;

  private final T response;

  private final ApiError error;

  private ApiResult(boolean success, T response, ApiError error) {
    this.success = success;
    this.response = response;
    this.error = error;
  }

  public static <T> ApiResult<T> OK(T response) {
    return new ApiResult<T>(true,response,null);
  }

  public static <T> ApiResult<T> ERROR(Throwable throwable, int status) {
    return new ApiResult<>(false,null,new ApiError(throwable,status));
  }

  public static <T> ApiResult<T> ERROR(String message, int status) {
    return new ApiResult<>(false,null,new ApiError(message,status));
  }

}
