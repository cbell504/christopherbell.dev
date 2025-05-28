package dev.christopherbell.libs.api.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResourceExistsExceptionTest {

  @Test
  public void testResourceExistsException() {
    var exception = ResourceExistsException.builder().build();

    Assertions.assertNotNull(exception);
  }

  @Test
  public void testResourceExistExceptionWithMessage() {
    var exception = new ResourceExistsException("Resource Not Found");

    Assertions.assertNotNull(exception);
    Assertions.assertEquals("Resource Not Found", exception.getMessage());
  }

  @Test
  public void testResourceExistsExceptionWithMessageAndException() {
    var exception = new ResourceExistsException("Resource Not Found", new Exception("Another Exception"));

    Assertions.assertNotNull(exception);
    Assertions.assertEquals("Resource Not Found", exception.getMessage());
    Assertions.assertNotNull(exception.getCause());
    Assertions.assertEquals("Another Exception", exception.getCause().getMessage());
  }

  @Test
  public void testResourceExistsExceptionWithException() {
    var exception = new ResourceExistsException(new Exception("Another Exception"));

    Assertions.assertNotNull(exception.getCause());
    Assertions.assertEquals("Another Exception", exception.getCause().getMessage());
  }
}