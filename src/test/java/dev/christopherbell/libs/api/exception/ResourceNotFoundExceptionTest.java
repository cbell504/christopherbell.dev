package dev.christopherbell.libs.api.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResourceNotFoundExceptionTest {

  @Test
  public void testNotFoundException() {
    var exception = ResourceNotFoundException.builder().build();

    Assertions.assertNotNull(exception);
  }

  @Test
  public void testNotFoundExceptionWithMessage() {
    var exception = new ResourceNotFoundException("Account Not Found");

    Assertions.assertNotNull(exception);
    Assertions.assertEquals("Account Not Found", exception.getMessage());
  }

  @Test
  public void testNotFoundExceptionWithMessageAndException() {
    var exception = new ResourceNotFoundException("Account Not Found", new Exception("Another Exception"));

    Assertions.assertNotNull(exception);
    Assertions.assertEquals("Account Not Found", exception.getMessage());
    Assertions.assertNotNull(exception.getCause());
    Assertions.assertEquals("Another Exception", exception.getCause().getMessage());
  }

  @Test
  public void testNotFoundExceptionWithException() {
    var exception = new ResourceNotFoundException(new Exception("Another Exception"));

    Assertions.assertNotNull(exception.getCause());
    Assertions.assertEquals("Another Exception", exception.getCause().getMessage());
  }
}