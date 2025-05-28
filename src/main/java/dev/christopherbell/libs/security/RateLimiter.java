package dev.christopherbell.libs.security;

import dev.christopherbell.libs.api.exception.TooManyRequestsException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RateLimiter {

  public static final long ENTRY_EXPIRY_THRESHOLD = 300_000; // 5 min
  public static final long CLEANUP_INTERVAL = 60_000; // every 1 min

  @Value("${rate.limiter.intervalMillis:60000}")
  protected long intervalMillis;
  @Value("${rate.limiter.maxRequests:100}")
  protected int maxRequests;
  final Map<String, RateLimitInfo> map = new ConcurrentHashMap<>();
  private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();


  @PostConstruct
  public void startCleanupTask() {
    cleanupExecutor.scheduleAtFixedRate(
        this::cleanupStaleEntries,
        CLEANUP_INTERVAL,
        CLEANUP_INTERVAL,
        TimeUnit.MILLISECONDS);
  }

  @PreDestroy
  public void shutdown() {
    cleanupExecutor.shutdown();
  }

  public boolean isAllowed(HttpServletRequest request) {
    String ip = getClientIp(request);
    RateLimitInfo info = map.computeIfAbsent(ip, k -> new RateLimitInfo(intervalMillis, maxRequests));
    return info.allowRequest();
  }

  private String getClientIp(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0].trim();
  }

  protected void cleanupStaleEntries() {
    long now = System.currentTimeMillis();
    map.entrySet()
        .removeIf(entry -> now - entry.getValue().getLastReset() > ENTRY_EXPIRY_THRESHOLD);
  }

  public void checkRequest(HttpServletRequest request) {
    if (!isAllowed(request)) {
      throw new TooManyRequestsException("Rate limit exceeded. Please try again later.");
    }
  }

  private static class RateLimitInfo {
    private final long intervalMillis;
    private final int maxRequests;
    private final AtomicInteger count = new AtomicInteger(0);
    @Getter
    private volatile long lastReset = System.currentTimeMillis();

    public RateLimitInfo(long intervalMillis, int maxRequests) {
      this.intervalMillis = intervalMillis;
      this.maxRequests = maxRequests;
    }

    public boolean allowRequest() {
      long now = System.currentTimeMillis();
      if (now - lastReset > intervalMillis) {
        count.set(1);
        lastReset = now;
        return true;
      } else {
        int current = count.incrementAndGet();
        return current <= maxRequests;
      }
    }
  }
}
