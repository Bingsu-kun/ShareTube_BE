package com.takarabako.sharetube.error;

import com.takarabako.sharetube.util.MessageUtils;

public class JsonParseException extends ServiceRuntimeException {
  public static final String MESSAGE_KEY = "error.jsonparse";

  public static final String MESSAGE_DETAIL = "error.jsonparse.details";

  public JsonParseException(String message) {
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
