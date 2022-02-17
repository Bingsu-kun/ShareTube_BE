package com.takarabako.sharetube.model.common;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiError {

  private final String message;

  private final int status;

  public ApiError(String message, int status) {
    this.message = message;
    this.status = status;
  }

  public ApiError(Throwable throwable, int status) {
    this(throwable.getMessage(),status);
  }

}
