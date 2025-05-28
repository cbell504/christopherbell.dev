package dev.christopherbell.libs.security;

import dev.christopherbell.account.model.entity.AccountEntity;
import io.jsonwebtoken.Claims;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final PermissionService permissionService;
  private final List<RequestMatcher> skipMatchers;

  public JwtAuthenticationFilter(
      PermissionService permissionService,
      String[] skipUrls
  ) {
    this.permissionService = permissionService;
    this.skipMatchers = Arrays.stream(skipUrls).<RequestMatcher>map(AntPathRequestMatcher::new).toList();
  }

  /**
   * Returns true if any skipMatcher matches the request
   * @param request - request sent in by a client.
   * @return boolean
   */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return skipMatchers
        .stream()
        .anyMatch(matcher -> matcher.matches(request));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain
  ) throws ServletException, IOException {

    String token = resolveToken(request);
    if (token != null && Objects.nonNull(permissionService.validateToken(token))) {
      Authentication authenticationToken = getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      if (authenticationToken.isAuthenticated()) {
        chain.doFilter(request, response);
      } else {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      }
    }
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private Authentication getAuthentication(String token) {
    Claims claims = permissionService.validateToken(token);
    String username = claims.getSubject();
    String roles = claims.get(AccountEntity.PROPERTY_ROLE, String.class);
    List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
    return new UsernamePasswordAuthenticationToken(username, token, authorities);
  }

  private Authentication buildAuthentication(Claims claims, String token) {
    String username = claims.getSubject();
    String roles = claims.get(AccountEntity.PROPERTY_ROLE, String.class);
    List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
        .<GrantedAuthority>map(SimpleGrantedAuthority::new)
        .toList();
    return new UsernamePasswordAuthenticationToken(username, token, authorities);
  }
}
