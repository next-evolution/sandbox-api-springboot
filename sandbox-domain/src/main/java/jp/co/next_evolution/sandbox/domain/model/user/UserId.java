package jp.co.next_evolution.sandbox.domain.model.user;

import java.util.Base64;
import java.util.UUID;
import jp.co.next_evolution.sandbox.domain.exception.DomainValidationException;

public record UserId(String value) {

  public UserId {
    if (value == null || value.isBlank()) {
      throw new DomainValidationException("UserId must not be blank");
    }
    try {
      UUID.fromString(value);
    } catch (IllegalArgumentException e) {
      throw new DomainValidationException("UserId must be UUID format: " + value);
    }
  }

  public static UserId newId() {
    return new UserId(UUID.randomUUID().toString());
  }

  public static String decodeUserIdValue(String encodedUserId) {
    try {
      return new String(Base64.getDecoder().decode(encodedUserId));
    } catch (IllegalArgumentException e) {
      return "";
    }
  }

}
