package jp.co.next_evolution.sandbox.domain.exception;

public class GenesisApiException extends RuntimeException {

  public GenesisApiException(String message) {
    super(message);
  }

  public GenesisApiException(String message, Throwable cause) {
    super(message, cause);
  }

}
