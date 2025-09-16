package dev.christopherbell.configuration;

import dev.christopherbell.account.model.Account;
import dev.christopherbell.permission.PermissionService;
import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.core.annotation.Order;

/**
 * Servlet filter that authenticates requests using a JWT found in the
 * {@code Authorization: Bearer <token>} header.
 *
 * <p>Skips paths matched by the configured {@link RequestMatcher}s. When a
 * valid token is present, sets the Spring Security {@link Authentication}
 * into the {@link SecurityContextHolder}.</p>
 */
@Order(2)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final List<RequestMatcher> skipMatchers = new ArrayList<>();

  public JwtAuthenticationFilter(List<RequestMatcher> skipMatchers) {
    this.skipMatchers.addAll(skipMatchers);
  }

  /**
   * Determines whether this filter should be skipped for the given request.
   *
   * @param request incoming HTTP request
   * @return {@code true} if any configured skip matcher matches
   */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    boolean isPublic = skipMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    // If request carries a Bearer token, do not skip — authenticate even on public routes
    String auth = request.getHeader("Authorization");
    boolean hasBearer = auth != null && auth.startsWith("Bearer ");
    return isPublic && !hasBearer;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String token = resolveToken(request);
    if (token == null) {
      // No token: continue as anonymous; downstream security will enforce access rules
      chain.doFilter(request, response);
      return;
    }
    try {
      if (Objects.nonNull(PermissionService.validateToken(token))) {
        Authentication authenticationToken = getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        if (authenticationToken.isAuthenticated()) {
          chain.doFilter(request, response);
        } else {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
      }
    } catch (Exception e) {
      // Invalid token
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
  }

  /**
   * Extracts the bearer token from the {@code Authorization} header.
   *
   * @param request current HTTP request
   * @return the JWT value, or {@code null} if header is missing or not a bearer token
   */
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  /**
   * Builds an {@link Authentication} from a validated JWT.
   *
   * @param token the raw JWT token
   * @return a {@link UsernamePasswordAuthenticationToken} populated with subject and authorities
   */
  private Authentication getAuthentication(String token) {
    Claims claims = PermissionService.validateToken(token);
    String username = claims.getSubject();
    String roles = claims.get(Account.PROPERTY_ROLE, String.class);
    List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
    return new UsernamePasswordAuthenticationToken(username, token, authorities);
  }
}
