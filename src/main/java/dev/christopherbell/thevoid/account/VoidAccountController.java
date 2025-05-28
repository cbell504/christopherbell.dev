//package dev.christopherbell.thevoid.account;
//
//import dev.christopherbell.libs.api.model.Response;
//import dev.christopherbell.libs.api.exception.ResourceNotFoundException;
//import dev.christopherbell.libs.api.exception.InvalidRequestException;
//import dev.christopherbell.libs.api.exception.InvalidTokenException;
//import dev.christopherbell.libs.api.exception.ResourceExistsException;
//import dev.christopherbell.libs.security.PermissionService;
//import dev.christopherbell.thevoid.account.model.dto.AccountResponse;
//import dev.christopherbell.thevoid.account.model.dto.AccountsResponse;
//import dev.christopherbell.thevoid.common.VoidRequest;
//import dev.christopherbell.thevoid.common.VoidResponse;
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@AllArgsConstructor
//@RequestMapping("/api/thevoid/accounts")
//@RestController
//public class VoidAccountController {
//
//  private final PermissionService permissionService;
//  private final VoidAccountService voidAccountService;
//
//  /**
//   * Get all accounts on file, clientId is required.
//   *
//   * @param clientId - String value that represents the client's id
//   * @return all accounts
//   * @throws InvalidRequestException - thrown is request is considered invalid
//   */
//  @GetMapping(value = "/v1", produces = MediaType.APPLICATION_JSON_VALUE)
//  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
//  public ResponseEntity<Response<AccountsResponse>> getAccounts(@RequestHeader String clientId)
//      throws InvalidRequestException {
//    return new ResponseEntity<>(Response.<AccountsResponse>builder()
//        .payload(voidAccountService.getAccounts(clientId))
//        .success(true)
//        .build(), HttpStatus.OK);
//  }
//
//  /**
//   * Get an account on file by a given accountId
//   *
//   * @param clientId  - String value that represents the client's id
//   * @param accountId - Long value that represents the user's account id
//   * @return an account for the given accountId
//   * @throws InvalidRequestException  - thrown if client id is not found
//   * @throws ResourceNotFoundException - thrown if no accounts are on file with that id
//   */
//  @GetMapping(value = "/v1/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
//  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
//  public ResponseEntity<Response<AccountResponse>> getAccountById(@RequestHeader String clientId,
//      @PathVariable Long accountId) throws InvalidRequestException, ResourceNotFoundException {
//    return new ResponseEntity<>(Response.<AccountResponse>builder()
//        .payload(voidAccountService.getAccountById(clientId, accountId))
//        .success(true)
//        .build(), HttpStatus.OK);
//  }
//
//  /**
//   * Get an account on file by a given account username
//   *
//   * @param clientId - String value that represents the client's id
//   * @param username - String value that represents the account username
//   * @return an account for the given username
//   * @throws InvalidRequestException  - thrown if client id is not found
//   * @throws ResourceNotFoundException - thrown if no accounts are on file with that username
//   */
//  @GetMapping(value = "/v1/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
//  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
//  public ResponseEntity<Response<VoidResponse>> getAccountByUsername(@RequestHeader String clientId,
//      @PathVariable String username) throws InvalidRequestException, ResourceNotFoundException {
//    return new ResponseEntity<>(Response.<VoidResponse>builder()
//        .payload(voidAccountService.getAccountByUsername(clientId, username))
//        .success(true)
//        .build(), HttpStatus.OK);
//  }
//
//  /**
//   * Creates an account with the given request body.
//   *
//   * @param clientId    - String value that represents the client's id
//   * @param voidRequest - Object containing details about the account to create
//   * @return an account object if the creation was successful
//   * @throws InvalidRequestException        - thrown if client id is not found
//   * @throws ResourceExistsException - thrown if no accounts are on file with that username
//   */
//  @PostMapping(value = "/v1/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
//  public ResponseEntity<Response<VoidResponse>> createAccount(@RequestHeader String clientId,
//      @RequestBody VoidRequest voidRequest) throws InvalidRequestException, ResourceExistsException {
//    return new ResponseEntity<>(Response.<VoidResponse>builder()
//        .payload(voidAccountService.createAccount(clientId, voidRequest))
//        .success(true)
//        .build(), HttpStatus.CREATED);
//  }
//
//  /**
//   * @param clientId
//   * @param loginToken
//   * @param voidRequest
//   * @return
//   * @throws InvalidRequestException
//   * @throws ResourceExistsException
//   * @throws ResourceNotFoundException
//   * @throws InvalidTokenException
//   */
//  @GetMapping(value = "/v1/active", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
//  public ResponseEntity<Response<AccountResponse>> getActiveAccount(@RequestHeader String clientId,
//      @RequestHeader String loginToken,
//      @RequestBody VoidRequest voidRequest)
//      throws InvalidRequestException, ResourceExistsException, ResourceNotFoundException, InvalidTokenException {
//    return new ResponseEntity<>(Response.<AccountResponse>builder()
//        .payload(voidAccountService.getActiveAccount(clientId, loginToken, voidRequest))
//        .success(true)
//        .build(), HttpStatus.OK);
//  }
//
//  /**
//   * Returns a loginToken once a user has been authenticated
//   *
//   * @param clientId    - String value that represents the client's id
//   * @param voidRequest Object containing details about the account
//   * @return a void response and a header containing the loginToken
//   * @throws ResourceNotFoundException - thrown if no accounts are on file with that username
//   * @throws InvalidRequestException  - thrown if client id is not found
//   * @throws InvalidTokenException    - thrown is login info is not correct
//   */
//  @PostMapping(value = "/v1/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
//  public ResponseEntity<Response<VoidResponse>> loginAccount(@RequestHeader String clientId,
//      @RequestBody VoidRequest voidRequest)
//      throws InvalidRequestException, ResourceNotFoundException, InvalidTokenException {
//    var response = this.voidAccountService.loginAccount(clientId, voidRequest);
//    return new ResponseEntity<>(Response.<VoidResponse>builder()
//        .payload(response)
//        .success(true)
//        .build(), response.getHttpHeaders(), HttpStatus.OK);
//  }
//
//  /**
//   * @param clientId
//   * @param loginToken
//   * @param accountId
//   * @return
//   * @throws InvalidRequestException
//   * @throws ResourceNotFoundException
//   * @throws InvalidTokenException
//   */
//  @GetMapping(value = "/v1/logout/{accountId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
//  public ResponseEntity<Response<VoidResponse>> logoutAccount(@RequestHeader String clientId,
//      @RequestHeader String loginToken,
//      @PathVariable Long accountId)
//      throws InvalidRequestException, ResourceNotFoundException, InvalidTokenException {
//    var response = this.voidAccountService.logoutAccount(clientId, loginToken, accountId);
//    return new ResponseEntity<>(Response.<VoidResponse>builder()
//        .payload(response)
//        .success(true)
//        .build(), response.getHttpHeaders(), HttpStatus.OK);
//  }
//
//  /**
//   * @param clientId
//   * @param loginToken
//   * @param accountID
//   * @param voidRequest
//   * @return
//   * @throws InvalidRequestException
//   */
//  @PatchMapping(value = "/v1/{accountId}/updateRole", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
//  public ResponseEntity<Response<VoidResponse>> updateRole(@RequestHeader String clientId,
//      @RequestHeader String loginToken,
//      @PathVariable Long accountID,
//      @RequestBody VoidRequest voidRequest) throws InvalidRequestException {
//    return new ResponseEntity<>(Response.<VoidResponse>builder()
//        .payload(voidAccountService.updateRole(clientId, accountID, voidRequest))
//        .success(true)
//        .build(), HttpStatus.OK);
//  }
//}
