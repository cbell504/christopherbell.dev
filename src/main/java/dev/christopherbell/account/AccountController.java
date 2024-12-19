package dev.christopherbell.account;

import dev.christopherbell.account.models.Account;
import dev.christopherbell.libs.common.api.exceptions.InvalidRequestException;
import dev.christopherbell.libs.common.api.exceptions.InvalidTokenException;
import dev.christopherbell.libs.common.api.exceptions.ResourceNotFoundException;
import dev.christopherbell.libs.common.api.models.Response;
import dev.christopherbell.permission.PermissionService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/api/accounts")
@RestController
public class AccountController {

  private AccountService accountService;
  private PermissionService permissionService;
  public static final String VERSION_DECEMBER_15_2024 = "/20241215";

  @PostMapping(value = VERSION_DECEMBER_15_2024,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Account>> createAccount(@RequestBody Account account) throws InvalidRequestException {

    return new ResponseEntity<>(
        Response.<Account>builder()
            .payload(accountService.createAccount(account))
            .success(true)
            .build(), HttpStatus.OK);
  }

  @GetMapping(value = VERSION_DECEMBER_15_2024 + "/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
  public ResponseEntity<Response<Account>> getAccountById(@PathVariable String email) throws ResourceNotFoundException {

    return new ResponseEntity<>(
        Response.<Account>builder()
            .payload(accountService.getAccount(email))
            .success(true)
            .build(), HttpStatus.OK);
  }

  @GetMapping(value = VERSION_DECEMBER_15_2024 + "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
  public ResponseEntity<Response<List<Account>>> getAccounts() {

    return new ResponseEntity<>(
        Response.<List<Account>>builder()
            .payload(accountService.getAccounts())
            .success(true)
            .build(), HttpStatus.OK);
  }

  @PostMapping(value = VERSION_DECEMBER_15_2024 + "/login",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<String>> loginAccount(@RequestBody Account account) throws InvalidTokenException {

    return new ResponseEntity<>(Response.<String>builder()
        .payload(accountService.loginAccount(account))
        .success(true)
        .build(), HttpStatus.OK);
  }

}
