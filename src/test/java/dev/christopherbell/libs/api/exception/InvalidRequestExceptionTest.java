package dev.christopherbell.libs.api.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InvalidRequestExceptionTest {

  @Test
  public void testInvalidRequestException() {
    var exception = InvalidRequestException.builder().build();

    Assertions.assertNotNull(exception);
  }

  @Test
  public void testInvalidRequestExceptionWithMessage() {
    var exception = new InvalidRequestException("Invalid Request");

    Assertions.assertNotNull(exception);
    Assertions.assertEquals("Invalid Request", exception.getMessage());
  }

  @Test
  public void testInvalidRequestExceptionWithMessageAndException() {
    var exception = new InvalidRequestException("Invalid Request", new Exception("Another Exception"));

    Assertions.assertNotNull(exception);
    Assertions.assertEquals("Invalid Request", exception.getMessage());
    Assertions.assertNotNull(exception.getCause());
    Assertions.assertEquals("Another Exception", exception.getCause().getMessage());
  }

  @Test
  public void testInvalidRequestExceptionWithException() {
    var exception = new InvalidRequestException(new Exception("Another Exception"));

    Assertions.assertNotNull(exception.getCause());
    Assertions.assertEquals("Another Exception", exception.getCause().getMessage());
  }
}