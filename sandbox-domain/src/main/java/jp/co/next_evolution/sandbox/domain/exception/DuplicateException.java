package jp.co.next_evolution.sandbox.domain.exception;

public class DuplicateException extends RuntimeException {

  public DuplicateException(String message) {
    super("duplicate: " + message);
  }

}
