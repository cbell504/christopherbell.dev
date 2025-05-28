package dev.christopherbell.libs.api.exception;

import lombok.Builder;

/**
 * Exception to throw if we cannot find an account for a request.
 */
@Builder
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException() {
    super();
  }

  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(Throwable cause) {
    super(cause);
  }
}