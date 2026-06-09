package jp.co.next_evolution.sandbox.domain.model.user;

import java.util.Base64;
import jp.co.next_evolution.sandbox.domain.exception.DomainValidationException;

public record Email(String value) {

  public Email {
    if (value == null || !value.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) {
      throw new DomainValidationException("Invalid Email format: " + value);
    }
  }

  public static String decodeEmail(String encodedEmail) {
    try {
      return new String(Base64.getDecoder().decode(encodedEmail));
    } catch (IllegalArgumentException e) {
      throw new DomainValidationException("Invalid BASE64 email");
    }
  }

  public boolean matches(Email other) {
    return this.value.equalsIgnoreCase(other.value);
  }

}