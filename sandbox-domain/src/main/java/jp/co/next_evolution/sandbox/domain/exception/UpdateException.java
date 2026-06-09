package jp.co.next_evolution.sandbox.domain.exception;

public class UpdateException extends RuntimeException {

  public UpdateException(String message) {
    super("DB update error: " + message);
  }

}
