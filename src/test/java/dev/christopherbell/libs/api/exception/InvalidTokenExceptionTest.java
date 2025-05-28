package dev.christopherbell.libs.api.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InvalidTokenExceptionTest {

  @Test
  public void testInvalidTokenException() {
    var exception = InvalidTokenException.builder().build();

    Assertions.assertNotNull(exception);
  }

  @Test
  public void testInvalidTokenExceptionWithMessage() {
    var exception = new InvalidTokenException("Invalid Token");

    Assertions.assertNotNull(exception);
    Assertions.assertEquals("Invalid Token", exception.getMessage());
  }

  @Test
  public void testInvalidTokenExceptionWithMessageAndException() {
    var exception = new InvalidTokenException("Invalid Token", new Exception("Another Exception"));

    Assertions.assertNotNull(exception);
    Assertions.assertEquals("Invalid Token", exception.getMessage());
    Assertions.assertNotNull(exception.getCause());
    Assertions.assertEquals("Another Exception", exception.getCause().getMessage());
  }

  @Test
  public void testInvalidTokenExceptionWithException() {
    var exception = new InvalidTokenException(new Exception("Another Exception"));

    Assertions.assertNotNull(exception.getCause());
    Assertions.assertEquals("Another Exception", exception.getCause().getMessage());
  }

}