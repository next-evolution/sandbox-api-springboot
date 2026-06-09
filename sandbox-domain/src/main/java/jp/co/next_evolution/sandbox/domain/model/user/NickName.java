package jp.co.next_evolution.sandbox.domain.model.user;

import jp.co.next_evolution.sandbox.domain.exception.DomainValidationException;

public record NickName(String value) {

  public NickName {
    if (value == null || value.isBlank()) {
      throw new DomainValidationException("NickName must not be blank");
    }
    if (value.length() > 50) {
      throw new DomainValidationException("NickName too long: " + value);
    }
  }

}