package dev.christopherbell.libs.api.util;

import dev.christopherbell.libs.api.exception.InvalidRequestException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class APIValidationUtilsTest {

  private static final String TEST_CLIENT_ID = "testClientId";

  @Test
  public void testIsValidClientId_success() throws InvalidRequestException {
    var result = APIValidationUtils.isValidClientId(List.of(TEST_CLIENT_ID), TEST_CLIENT_ID);

    Assertions.assertTrue(result);
  }

  @Test
  public void testIsValidClientId_failure_exceptionThrow() {

    var exception = Assertions.assertThrows(InvalidRequestException.class, () -> {
      APIValidationUtils.isValidClientId(List.of(TEST_CLIENT_ID), "");
    });

    Assertions.assertEquals(APIConstants.VALIDATION_BAD_CLIENT_ID, exception.getMessage());
  }

  @Test
  public void testIsValidResource_success() throws InvalidRequestException {
    var result = APIValidationUtils.isValidResource(APIConstants.VALIDATION_BAD_EMAIL, "test@test.com");

    Assertions.assertTrue(result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " "})
  public void testIsValidResource_failure(String resource) {

    var exception = Assertions.assertThrows(InvalidRequestException.class, () -> {
      APIValidationUtils.isValidResource(APIConstants.VALIDATION_BAD_EMAIL, resource);
    });

    Assertions.assertEquals(APIConstants.VALIDATION_BAD_EMAIL, exception.getMessage());
  }

  @Test
  public void testIsValidResource_failure_nullResource() {

    var exception = Assertions.assertThrows(InvalidRequestException.class, () -> {
      APIValidationUtils.isValidResource(APIConstants.VALIDATION_BAD_EMAIL, null);
    });

    Assertions.assertEquals(APIConstants.VALIDATION_BAD_EMAIL, exception.getMessage());
  }

}