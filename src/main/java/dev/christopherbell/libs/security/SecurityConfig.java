package dev.christopherbell.libs.security;

import dev.christopherbell.account.AccountController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private static final String[] PUBLIC_URLS = {
      "/",
      "/api/accounts" + AccountController.V20241215 + "/authenticate",
      "/api/accounts" + AccountController.V20241215 + "/create",
      "/blog",
      "/css/**",
      "/js/**",
      "/login",
      "/photos",
      "/photos/**",
      "/signup",
      "/thebell/**",
      "/void",
      "/void/**",
      "/wfl"
  };

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter
  ) throws Exception {
    return http
        // Disable CSRF for APIs (use with care)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Configure authorization rules
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(PUBLIC_URLS).permitAll() // Allow public access to defined URLs
            .anyRequest().authenticated() // Secure all other endpoints
        )
        .exceptionHandling(eh -> eh.authenticationEntryPoint(
            (req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
        // Add custom JWT authentication filter
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(PermissionService permissionService) {
    return new JwtAuthenticationFilter(permissionService, PUBLIC_URLS);
  }
}
