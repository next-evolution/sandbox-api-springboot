package jp.co.next_evolution.sandbox.domain.exception;

public class InsertException extends RuntimeException {

  public InsertException(String message) {
    super("DB insert error: " + message);
  }

}
