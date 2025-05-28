package dev.christopherbell.libs.security;

import dev.christopherbell.account.model.entity.AccountEntity;
import dev.christopherbell.libs.api.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PermissionService {

//  private static final String SECRET_KEY = generateKey();
//  private static final long EXPIRATION_TIME = 3600_000; // 1 hour in milliseconds
//  private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
  private static final Duration EXPIRATION = Duration.ofHours(1);

  @Value("${security.jwt.secret:}")
  private String secretKeyProp;
  private Key signingKey;

  @PostConstruct
  private void init() {
    if (secretKeyProp == null || secretKeyProp.isBlank()) {
      secretKeyProp = generateRandomKey();
      log.warn("no jwt secret provided – generated transient key (tokens won’t survive restart)");
    }
    signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyProp));
  }

  /**
   * Generates a JWT token with a key that was created on application startup.
   *
   * @param accountEntity - the account that will be getting the new token.
   * @return a JWT token in String format.
   */
  public String generateToken(AccountEntity accountEntity) {
    return Jwts.builder()
        .claims(Map.of(AccountEntity.PROPERTY_ROLE, accountEntity.getRole()))
        .id(UUID.randomUUID().toString())
        .subject(accountEntity.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION.toMillis()))
        .signWith(signingKey)
        .compact();
  }

  /**
   * Validates a given JWT token with the key that was generated on application start up.
   *
   * @param token - the given JWT.
   * @return the claims for that JWT.
   */
  public Claims validateToken(String token) {
    return Jwts.parser()
        .verifyWith((SecretKey) signingKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * Generates a key for creating JWTs on application startup.
   *
   * @return return key in string form.
   */
  public static String generateKey() {
    try {
      KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
      keyGen.init(256);
      SecretKey secretKey = keyGen.generateKey();
      var base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
      System.out.println("Generated Key: " + base64Key);
      return base64Key;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return "";
    }
  }

  private static String generateRandomKey() {
    try {
      KeyGenerator gen = KeyGenerator.getInstance("HmacSHA256");
      gen.init(256);
      SecretKey key = gen.generateKey();
      return Base64.getEncoder().encodeToString(key.getEncoded());
    } catch (Exception e) {
      throw new IllegalStateException("unable to generate jwt key", e);
    }
  }

  /**
   * Checks to see if a user has some required role in order to continue with their request.
   *
   * @param requiredRole - The role required for the request.
   * @return boolean on if the requester has the required role or not.
   */
  public boolean hasAuthority(String requiredRole) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    try {
      String token = (String) authentication.getCredentials();
      Claims claims = validateToken(token);
      String roles  = claims.get(AccountEntity.PROPERTY_ROLE, String.class);
      return roles != null && roles.contains(requiredRole);
    } catch (Exception e) {
      log.error("Error validating token or extracting claims: {}", e.getMessage(), e);
      return false; // Deny access on any error
    }
  }

  public boolean isAccountApproved(AccountEntity accountEntity) throws InvalidTokenException {
    if (accountEntity.getIsApproved()) {
      return true;
    } else {
      throw new InvalidTokenException("Account is not approved.");
    }
  }
}
