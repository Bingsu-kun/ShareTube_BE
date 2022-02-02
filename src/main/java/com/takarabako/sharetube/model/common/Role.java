package com.takarabako.sharetube.model.common;

import lombok.Getter;

import java.util.Locale;

@Getter
public enum Role {

  ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

  private Role(String name) {}

  public static Role of(String name) {
    for ( Role i : Role.values() ) {
      if (i.name().equals(name.toUpperCase(Locale.ROOT)))
        return i;
    }

    return null;
  }

}
