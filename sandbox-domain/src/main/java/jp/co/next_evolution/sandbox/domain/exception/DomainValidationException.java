package jp.co.next_evolution.sandbox.domain.exception;

public class DomainValidationException extends RuntimeException {

  public DomainValidationException(String message) {
    super(message);
  }

}
