package dev.christopherbell.thevoid.account;

import dev.christopherbell.libs.common.api.exceptions.ResourceNotFoundException;
import dev.christopherbell.libs.common.api.exceptions.InvalidRequestException;
import dev.christopherbell.libs.common.api.exceptions.InvalidTokenException;
import dev.christopherbell.libs.common.api.exceptions.ResourceExistsException;
import dev.christopherbell.libs.common.api.utils.APIConstants;
import dev.christopherbell.libs.common.api.utils.APIValidationUtils;
import dev.christopherbell.thevoid.account.model.dto.VoidRolesEnum;
import dev.christopherbell.thevoid.account.model.dto.AccountResponse;
import dev.christopherbell.thevoid.account.model.dto.AccountsResponse;
import dev.christopherbell.thevoid.account.model.dto.Account;
import dev.christopherbell.thevoid.common.VoidRequest;
import dev.christopherbell.thevoid.common.VoidResponse;
import dev.christopherbell.thevoid.account.model.dto.AccountDetails;
import dev.christopherbell.thevoid.account.model.dto.AccountSecurity;
import dev.christopherbell.thevoid.permission.PermissionsService;
import dev.christopherbell.libs.common.api.utils.ValidateUtil;
import dev.christopherbell.thevoid.utils.mappers.MapStructMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

/**
 * Represents a service responsible for handling accounts and account information.
 */
@AllArgsConstructor
@Service
@Slf4j
public class VoidAccountService {

  private final VoidAccountMessenger voidAccountMessenger;
  private final VoidAccountRepository voidAccountRepository;
  private final MapStructMapper mapStructMapper;
  private final PermissionsService permissionsService;

  /**
   * @param clientId
   * @param voidRequest
   * @return
   * @throws InvalidRequestException
   * @throws ResourceExistsException
   */
  public VoidResponse createAccount(String clientId, VoidRequest voidRequest)
      throws InvalidRequestException, ResourceExistsException {
    log.info("Request to create a new account by clientId: {}", clientId);

    // Validate account
    ValidateUtil.validateAccount(voidRequest);

    // Validate clientId
    APIValidationUtils.isValidClientId(ValidateUtil.ACCEPTED_CLIENT_IDs, clientId);

    var account = voidRequest.getAccount();
    var accountDetails = Objects.requireNonNullElse(account.getAccountDetails(), new AccountDetails());
    var accountSecurity = Objects.requireNonNullElse(account.getAccountSecurity(), new AccountSecurity());
    account.setVoidRole(VoidRolesEnum.VOID_DWELLER);

    var username = ValidateUtil.getCleanUsername(account);
    APIValidationUtils.isValidResource(APIConstants.VALIDATION_BAD_USERNAME, username);

    var accountOptional = this.voidAccountRepository.findByUsername(username);
    if (accountOptional.isPresent()) {
      // The username already exist in our database so throw an exception
      throw new ResourceExistsException("Account with this username already exists");
    } else {
      var voidRoleEntity = this.mapStructMapper.mapToVoidRoleEntity(VoidRolesEnum.VOID_DWELLER);
      var accountEntity = this.mapStructMapper.mapToAccountEntity(account);
      accountEntity.setVoidRoleEntity(voidRoleEntity);
      var accountDetailsEntity = this.mapStructMapper.mapToAccountDetailsEntity(accountDetails);
      var timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
      accountDetailsEntity.setCreationDate(timeStamp);
      accountDetailsEntity.setLastLoginDate(timeStamp);
      accountDetailsEntity.setAccountEntity(accountEntity);
      var accountSecurityEntity = this.mapStructMapper.mapToAccountSecurityEntity(accountSecurity);
      accountSecurityEntity.setAccountEntity(accountEntity);

      var email = ValidateUtil.getCleanEmailAddress(accountSecurity);
      var jwt = this.permissionsService.generateJWT(email);

      accountSecurityEntity.setLoginToken(jwt);

      // Save all entities to the DB
      this.voidAccountMessenger.saveAccountRepository(accountEntity);
      this.voidAccountMessenger.saveAccountDetailsRepository(accountDetailsEntity);
      this.voidAccountMessenger.saveAccountSecurityRepository(accountSecurityEntity);

      account = this.mapStructMapper.mapToAccount(accountEntity);
      var httpHeader = new HttpHeaders();
      httpHeader.add("loginToken", jwt);

      return VoidResponse.builder()
          .accounts(List.of(account))
          .httpHeaders(httpHeader)
          .build();
    }
  }

  /**
   * Returns a list of all accounts.
   * @param clientId id of the request.
   * @return AccountResponse containing a list of accounts.
   * @throws InvalidRequestException if the client id is not valid.
   */
  public AccountsResponse getAccounts(String clientId) throws InvalidRequestException {
    log.info("Request for all Accounts by clientId: {}", clientId);

    // Validate clientId
    APIValidationUtils.isValidClientId(ValidateUtil.ACCEPTED_CLIENT_IDs, clientId);

    var accountEntities = voidAccountMessenger.getAccountEntities();
    var accounts = accountEntities.stream().map(accountEntity -> {
      var account = mapStructMapper.mapToAccount(accountEntity);
      var voidRoleEnum = VoidRolesEnum.valueOf(accountEntity.getVoidRoleEntity().getRole());
      account.setVoidRole(voidRoleEnum);
      return account;
    }).toList();

    return AccountsResponse.builder()
        .accounts(accounts)
        .build();
  }

  /**
   * @param clientId
   * @param accountId
   * @return
   * @throws InvalidRequestException
   * @throws ResourceNotFoundException
   */
  public AccountResponse getAccountById(String clientId, Long accountId)
      throws InvalidRequestException, ResourceNotFoundException {
    log.info("Request for account with id: {} by clientId: {}", accountId, clientId);

    // Validate clientId
    APIValidationUtils.isValidClientId(ValidateUtil.ACCEPTED_CLIENT_IDs, clientId);

    // Pull Account info
    var accountEntity = this.voidAccountMessenger.getAccountEntityById(accountId);
    var account = this.mapStructMapper.mapToAccount(accountEntity);
    var voidRoleEnum = VoidRolesEnum.valueOf(accountEntity.getVoidRoleEntity().getRole());
    account.setVoidRole(voidRoleEnum);

    return AccountResponse.builder()
        .account(account)
        .build();
  }

  /**
   * @param clientId    - id used to describe the client caller
   * @param loginToken  - token a user receives after logging into their account
   * @param voidRequest - standard request body for a Void API request
   * @return response with details of the current user.
   * @throws ResourceNotFoundException - if we can't match the username with one in the db
   * @throws InvalidRequestException  - if the requestBody is null
   */
  public AccountResponse getActiveAccount(String clientId, String loginToken, VoidRequest voidRequest)
      throws ResourceNotFoundException, InvalidRequestException, InvalidTokenException {
    log.info("Request for active account by clientId: {}", clientId);

    // Validate inputs
    ValidateUtil.validateAccount(voidRequest);

    // Validate clientId
    APIValidationUtils.isValidClientId(ValidateUtil.ACCEPTED_CLIENT_IDs, clientId);

    var account = voidRequest.getAccount();
    var username = ValidateUtil.getCleanUsername(account);

    var accountEntity = this.voidAccountMessenger.getAccountEntityByUsername(username);
    var accountSecurityEntity = accountEntity.getAccountSecurityEntity();
    var dbLoginToken = accountSecurityEntity.getLoginToken();

    if (!dbLoginToken.equals(loginToken)) {
      throw new InvalidTokenException("Login token is not valid");
    }

    return AccountResponse.builder()
        .account(this.mapStructMapper.mapToAccount(accountEntity))
        .build();
  }

  /**
   * @param clientId    - id used to describe the client caller
   * @param voidRequest - standard request body for a Void API request
   * @return
   * @throws InvalidRequestException
   * @throws ResourceNotFoundException
   */
  public VoidResponse loginAccount(String clientId, VoidRequest voidRequest)
      throws InvalidRequestException, ResourceNotFoundException, InvalidTokenException {
    log.info("Request to login by clientId: {}", clientId);

    // Validate inputs
    ValidateUtil.validateAccount(voidRequest);

    // Validate clientId
    APIValidationUtils.isValidClientId(ValidateUtil.ACCEPTED_CLIENT_IDs, clientId);

    var account = Objects.requireNonNullElse(voidRequest.getAccount(), new Account());
    var accountSecurity = Objects.requireNonNullElse(account.getAccountSecurity(), new AccountSecurity());

    // Clean up the email and password, then make sure they aren't empty
    var email = ValidateUtil.getCleanEmailAddress(accountSecurity);
    var password = ValidateUtil.getCleanPassword(accountSecurity);

    log.info("Validating email and password");
    APIValidationUtils.isValidResource(APIConstants.VALIDATION_BAD_EMAIL, email);
    APIValidationUtils.isValidResource(APIConstants.VALIDATION_BAD_PASSWORD, password);

    // Validate Password
    var isAccountPasswordValid = this.permissionsService.validatePassword(email, password);
    var response = new VoidResponse();

    // Verify that the passwords are the same
    if (!isAccountPasswordValid) {
      throw new InvalidTokenException("Token not valid");
    } else {
      // Generate and save the JWT to the DB
      var jwt = this.permissionsService.generateJWT(email);
      var accountSecurityEntity = this.voidAccountMessenger.getAccountSecurityEntityByEmail(email);
      accountSecurityEntity.setLoginToken(jwt);
      this.voidAccountMessenger.saveAccountSecurityRepository(accountSecurityEntity);
      // Pull our current account from accountSecurityEntity. We want to do this because when we first login,
      // the frontend won't know much about this account. We want to give them the basic items need to make more
      // request to the backend.
      var accountEntity = accountSecurityEntity.getAccountEntity();
      var httpHeader = new HttpHeaders();
      httpHeader.add("loginToken", jwt);

      return VoidResponse.builder()
          .httpHeaders(httpHeader)
          .myself(this.mapStructMapper.mapToAccount(accountEntity))
          .build();
    }
  }

  /**
   * @param clientId
   * @param loginToken
   * @param accountId
   * @return
   * @throws ResourceNotFoundException
   * @throws InvalidRequestException
   * @throws InvalidTokenException
   */
  public VoidResponse logoutAccount(String clientId, String loginToken, Long accountId)
      throws ResourceNotFoundException, InvalidRequestException, InvalidTokenException {
    log.info("Request to logout by clientId: {}", clientId);

    // Validate clientId
    APIValidationUtils.isValidClientId(ValidateUtil.ACCEPTED_CLIENT_IDs, clientId);

    var isLoginTokenValid = this.permissionsService.validateLoginToken(loginToken, accountId);
    if (!isLoginTokenValid) {
      throw new InvalidTokenException("Token is not valid.");
    }

    var accountEntity = this.voidAccountMessenger.getAccountEntityById(accountId);
    var accountSecurityEntity = accountEntity.getAccountSecurityEntity();
    accountSecurityEntity.setLoginToken("");

    return VoidResponse.builder().build();
  }

  /**
   * @param clientId
   * @param username
   * @return
   * @throws InvalidRequestException
   * @throws ResourceNotFoundException
   */
  public VoidResponse getAccountByUsername(String clientId, String username)
      throws InvalidRequestException, ResourceNotFoundException {
    log.info("Requesting account with username: {} by clientId: {}", username, clientId);

    // Validate clientId
    APIValidationUtils.isValidClientId(ValidateUtil.ACCEPTED_CLIENT_IDs, clientId);

    var accountEntity = this.voidAccountMessenger.getAccountEntityByUsername(username);
    var account = this.mapStructMapper.mapToAccount(accountEntity);
    var voidRoleEnum = VoidRolesEnum.valueOf(accountEntity.getVoidRoleEntity().getRole());
    account.setVoidRole(voidRoleEnum);

    return VoidResponse.builder()
        .accounts(List.of(account))
        .build();
  }

  /**
   * @param clientId
   * @param accountId
   * @param voidRequest
   * @return
   * @throws InvalidRequestException
   */
  public VoidResponse updateRole(String clientId, Long accountId, VoidRequest voidRequest)
      throws InvalidRequestException {

    log.info("Updating VoidRole for account with id: {} by clientID: {}", accountId, clientId);

    // Validate inputs
    ValidateUtil.validateAccount(voidRequest);

    // Validate clientId
    APIValidationUtils.isValidClientId(ValidateUtil.ACCEPTED_CLIENT_IDs, clientId);

    return VoidResponse.builder().build();
  }
}
