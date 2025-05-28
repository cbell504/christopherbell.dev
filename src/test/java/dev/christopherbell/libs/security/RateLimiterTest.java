package dev.christopherbell.libs.security;

import dev.christopherbell.libs.api.exception.TooManyRequestsException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RateLimiterTest {

  private RateLimiter rateLimiter;
  private HttpServletRequest request;

  @BeforeEach
  public void setup() {
    rateLimiter = new RateLimiter();
    // manually inject interval and maxRequests for test
    rateLimiter.intervalMillis = 1000; // 1 sec window for fast tests
    rateLimiter.maxRequests = 3;
    request = mock(HttpServletRequest.class);
    when(request.getRemoteAddr()).thenReturn("1.2.3.4");
  }

  @Test
  public void testWithinLimit() {
    assertDoesNotThrow(() -> {
      rateLimiter.checkRequest(request);
      rateLimiter.checkRequest(request);
      rateLimiter.checkRequest(request);
    });
  }

  @Test
  public void testExceedLimit() {
    rateLimiter.checkRequest(request);
    rateLimiter.checkRequest(request);
    rateLimiter.checkRequest(request);
    TooManyRequestsException ex = assertThrows(TooManyRequestsException.class, () -> {
      rateLimiter.checkRequest(request);
    });
    assertEquals("Rate limit exceeded. Please try again later.", ex.getMessage());
  }

  @Test
  public void testResetAfterInterval() throws InterruptedException {
    rateLimiter.checkRequest(request);
    rateLimiter.checkRequest(request);
    rateLimiter.checkRequest(request);
    Thread.sleep(1100); // wait for interval reset
    assertDoesNotThrow(() -> rateLimiter.checkRequest(request));
  }

  @Test
  public void testIpIsolation() {
    HttpServletRequest request2 = mock(HttpServletRequest.class);
    when(request2.getRemoteAddr()).thenReturn("5.6.7.8");

    // spam IP1
    rateLimiter.checkRequest(request);
    rateLimiter.checkRequest(request);
    rateLimiter.checkRequest(request);
    assertThrows(TooManyRequestsException.class, () -> rateLimiter.checkRequest(request));

    // IP2 should be unaffected
    assertDoesNotThrow(() -> rateLimiter.checkRequest(request2));
  }

  @Test
  public void testEdgeCaseAtLimit() {
    for (int i = 0; i < rateLimiter.maxRequests; i++) {
      assertDoesNotThrow(() -> rateLimiter.checkRequest(request));
    }
    assertThrows(TooManyRequestsException.class, () -> rateLimiter.checkRequest(request));
  }
}
