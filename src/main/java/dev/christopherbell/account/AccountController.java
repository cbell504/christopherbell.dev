package dev.christopherbell.account;

import dev.christopherbell.account.model.dto.Account;
import dev.christopherbell.libs.api.exception.InvalidRequestException;
import dev.christopherbell.libs.api.exception.InvalidTokenException;
import dev.christopherbell.libs.api.exception.ResourceNotFoundException;
import dev.christopherbell.libs.api.model.Response;
import dev.christopherbell.libs.security.PermissionService;
import dev.christopherbell.libs.security.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/accounts")
@RestController
public class AccountController {

  public static final String V20241215 = "2024-12-15";
  public static final String V20250526 = "2025-05-26";

  private final AccountService accountService;
  private final PermissionService permissionService;
  private final RateLimiter rateLimiter;


  @PostMapping(
      value = V20241215 + "/create",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Response<Account>> createAccount(
      HttpServletRequest request,
      @RequestBody Account account
  ) {

    rateLimiter.checkRequest(request);
    var response = accountService.createAccount(account);

    return new ResponseEntity<>(
        Response.<Account>builder()
            .payload(response)
            .success(true)
            .build(),
        HttpStatus.CREATED);
  }

  @DeleteMapping(
      value = V20250526 + "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
  public ResponseEntity<Response<Account>> deleteAccount(
      HttpServletRequest request,
      @PathVariable String id
  ) {

    rateLimiter.checkRequest(request);
    var account = new Account();
    account.setRowKey(id);
    var response = accountService.deleteAccount(account);

    return new ResponseEntity<>(
        Response.<Account>builder()
            .payload(response)
            .success(true)
            .build(),
        HttpStatus.OK);
  }

  @GetMapping(value = V20241215 + "/email", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
  public ResponseEntity<Response<Account>> getAccountByEmail(
      HttpServletRequest request,
      @RequestParam String email
  ) {
    rateLimiter.checkRequest(request);
    var result = accountService.getAccountByEmail(email);

    return new ResponseEntity<>(
        Response.<Account>builder()
            .payload(result)
            .success(true)
            .build(),
        HttpStatus.OK);
  }

  @GetMapping(
      value = V20250526 + "/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
  public ResponseEntity<Response<Account>> getAccountById(
      HttpServletRequest request,
      @PathVariable String id
  ) {
    rateLimiter.checkRequest(request);
    var response = accountService.getAccountById(id);

    return new ResponseEntity<>(
        Response.<Account>builder()
            .payload(response)
            .success(true)
            .build(),
        HttpStatus.OK);
  }

  @GetMapping(value = V20241215, produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
  public ResponseEntity<Response<List<Account>>> getAccounts(
      HttpServletRequest request
  ) {
    rateLimiter.checkRequest(request);
    var response = accountService.getAccounts();

    return new ResponseEntity<>(
        Response.<List<Account>>builder()
            .payload(response)
            .success(true)
            .build(),
        HttpStatus.OK);
  }

  @PostMapping(value = V20241215 + "/authenticate",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<String>> loginAccount(
      HttpServletRequest request,
      @RequestBody Account account
  ) {
    rateLimiter.checkRequest(request);
    var response = accountService.loginAccount(account);

    return new ResponseEntity<>(
        Response.<String>builder()
            .payload(response)
            .success(true)
            .build(),
        HttpStatus.OK);
  }

}
