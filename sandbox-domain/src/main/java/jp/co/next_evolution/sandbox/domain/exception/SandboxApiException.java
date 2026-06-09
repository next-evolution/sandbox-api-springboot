package jp.co.next_evolution.sandbox.domain.exception;

public class SandboxApiException extends RuntimeException {

  public SandboxApiException(String message) {
    super(message);
  }

  public SandboxApiException(String message, Throwable cause) {
    super(message, cause);
  }

}
