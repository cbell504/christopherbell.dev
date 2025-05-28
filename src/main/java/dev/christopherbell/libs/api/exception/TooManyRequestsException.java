package dev.christopherbell.libs.api.exception;

public class TooManyRequestsException extends RuntimeException {
  public TooManyRequestsException(String message) {
    super(message);
  }
}
