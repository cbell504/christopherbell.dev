package dev.christopherbell.libs.api.exception;

import lombok.Builder;

/**
 * Exception to throw if something already exist in our DB with some unique field.
 */
@Builder
public class ResourceExistsException extends Exception {

  public ResourceExistsException() {
    super();
  }

  public ResourceExistsException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResourceExistsException(String message) {
    super(message);
  }

  public ResourceExistsException(Throwable cause) {
    super(cause);
  }
}