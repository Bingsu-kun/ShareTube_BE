package com.takarabako.sharetube.error;

import com.takarabako.sharetube.util.MessageUtils;

public class NotFoundException extends ServiceRuntimeException {

  public static final String MESSAGE_KEY = "error.notfound";

  public static final String MESSAGE_DETAIL = "error.notfound.details";

  public NotFoundException(String message) {
    super(MESSAGE_KEY, MESSAGE_DETAIL, new Object[]{message});
  }

  @Override
  public String getMessage() {
    return MessageUtils.getMessage(getDetailKey(), getParams());
  }

  @Override
  public String toString() {
    return MessageUtils.getMessage(getMessageKey());
  }

}
