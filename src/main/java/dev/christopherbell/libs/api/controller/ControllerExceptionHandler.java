package dev.christopherbell.libs.api.controller;

import dev.christopherbell.libs.api.exception.TooManyRequestsException;
import dev.christopherbell.libs.api.model.ErrorCode;
import dev.christopherbell.libs.api.model.Message;
import dev.christopherbell.libs.api.model.Response;
import dev.christopherbell.libs.api.exception.ResourceNotFoundException;
import dev.christopherbell.libs.api.exception.InvalidRequestException;
import dev.christopherbell.libs.api.exception.InvalidTokenException;
import dev.christopherbell.libs.api.exception.ResourceExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Response<?>> handleGenericException(Exception e) {
    log.error("Unhandled exception: {}", e.getMessage(), e);
    return new ResponseEntity<>(
        Response.builder()
            .messages(
                List.of(
                    Message.builder()
                        .code(ErrorCode.GENERIC_ERROR.getCode())
                        .description("An unexpected error occurred. Please contact support.")
                        .build()))
            .success(false)
            .build(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({InvalidRequestException.class})
  public ResponseEntity<Response<?>> handleInvalidRequestException(InvalidRequestException e) {
    log.error(e.getMessage(), e);
    return new ResponseEntity<>(
        Response.builder()
            .messages(
                List.of(
                    Message.builder()
                        .code(ErrorCode.INVALID_REQUEST.getCode())
                        .description(e.getMessage())
                        .build()))
            .success(false)
            .build(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<Response<?>> handleResourceNotFoundException(ResourceNotFoundException e) {
    log.error(e.getMessage(), e);
    return new ResponseEntity<>(
        Response.builder()
            .messages(
                List.of(
                    Message.builder()
                        .code(ErrorCode.ACCOUNT_NOT_FOUND.getCode())
                        .description(e.getMessage())
                        .build()))
            .success(false)
            .build(),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({ResourceExistsException.class})
  public ResponseEntity<Response<?>> handleResourceExistsException(ResourceExistsException e) {
    log.error(e.getMessage(), e);
    return new ResponseEntity<>(
        Response.builder()
            .messages(
                List.of(
                    Message.builder()
                        .code(ErrorCode.ACCOUNT_USER_NAME_EXISTS.getCode())
                        .description(e.getMessage())
                        .build()))
            .success(false)
            .build(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({InvalidTokenException.class})
  public ResponseEntity<Response<?>> handleInvalidTokenException(InvalidTokenException e) {
    log.error(e.getMessage(), e);
    return new ResponseEntity<>(
        Response.builder()
            .messages(
                List.of(
                    Message.builder()
                        .code(ErrorCode.INVALID_TOKEN.getCode())
                        .description(e.getMessage())
                        .build()))
            .success(false)
            .build(),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler({TooManyRequestsException.class})
  public ResponseEntity<Response<?>> handleTooManyRequests(TooManyRequestsException e) {
    log.warn(e.getMessage(), e);
    return new ResponseEntity<>(
        Response.builder()
            .messages(
                List.of(
                    Message.builder()
                        .code(ErrorCode.TOO_MANY_REQUESTS.getCode())
                        .description(e.getMessage())
                        .build()))
            .success(false)
            .build(),
        HttpStatus.TOO_MANY_REQUESTS);
  }
}
