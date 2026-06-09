package jp.co.next_evolution.sandbox.api.advice;

import jp.co.next_evolution.sandbox.api.dto.response.ErrorResponse;
import jp.co.next_evolution.sandbox.domain.exception.AuthenticationException;
import jp.co.next_evolution.sandbox.domain.exception.DuplicateException;
import jp.co.next_evolution.sandbox.domain.exception.ForbiddenException;
import jp.co.next_evolution.sandbox.domain.exception.InsertException;
import jp.co.next_evolution.sandbox.domain.exception.NotFoundException;
import jp.co.next_evolution.sandbox.domain.exception.UpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error(e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                 HttpStatus.INTERNAL_SERVER_ERROR.name(),
                                                 e.getMessage()));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException e) {
    log.error(e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                         .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                                                 HttpStatus.UNAUTHORIZED.name(),
                                                 e.getMessage()));
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException e) {
    log.error(e.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                         .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                                                 HttpStatus.FORBIDDEN.name(),
                                                 e.getMessage()));
  }

  @ExceptionHandler(DuplicateException.class)
  public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateException e) {
    log.error(e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                                 HttpStatus.BAD_REQUEST.name(),
                                                 e.getMessage()));
  }

  @ExceptionHandler(InsertException.class)
  public ResponseEntity<ErrorResponse> handleInsert(InsertException e) {
    log.error(e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                                 HttpStatus.BAD_REQUEST.name(),
                                                 e.getMessage()));
  }

  @ExceptionHandler(UpdateException.class)
  public ResponseEntity<ErrorResponse> handleUpdate(UpdateException e) {
    log.error(e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                                 HttpStatus.BAD_REQUEST.name(),
                                                 e.getMessage()));
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
    log.error(e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                         .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                                 HttpStatus.NOT_FOUND.name(),
                                                 e.getMessage()));
  }

}
