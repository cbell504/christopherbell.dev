package dev.christopherbell.account;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.models.TableServiceException;
import dev.christopherbell.account.models.Account;
import dev.christopherbell.account.models.AccountEntity;
import dev.christopherbell.account.models.Role;
import dev.christopherbell.libs.common.api.exceptions.InvalidRequestException;
import dev.christopherbell.libs.common.api.exceptions.InvalidTokenException;
import dev.christopherbell.libs.common.api.exceptions.ResourceNotFoundException;
import dev.christopherbell.libs.common.api.utils.PasswordUtils;
import dev.christopherbell.permission.PermissionService;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Represents the service responsible for handling getting, creating, updating, and deleting accounts.
 */
@AllArgsConstructor
@Slf4j
@Service
public class AccountService {

  private final AccountMapper accountMapper;
  private final TableClient tableClient;

  /**
   * Takes in an AccountEntity and returns back a TableEntity with all the properties from the AccountEntity.
   *
   * @param accountEntity - the AccountEntity to map to a TableEntity.
   * @return a TableEntity mapped from the given AccountEntity.
   */
  public TableEntity buildTableEntityFromAccountEntity(AccountEntity accountEntity) {
    return new TableEntity(
        AccountEntity.PARTITION_KEY,
        accountEntity.getEmail())
        .addProperty(AccountEntity.PROPERTY_APPROVED_BY, accountEntity.getApprovedBy())
        .addProperty(AccountEntity.PROPERTY_CREATED_ON, accountEntity.getCreatedOn())
        .addProperty(AccountEntity.PROPERTY_EMAIL, accountEntity.getEmail())
        .addProperty(AccountEntity.PROPERTY_FIRST_NAME, accountEntity.getFirstName())
        .addProperty(AccountEntity.PROPERTY_IS_APPROVED, accountEntity.getIsApproved())
        .addProperty(AccountEntity.PROPERTY_LAST_NAME, accountEntity.getLastName())
        .addProperty(AccountEntity.PROPERTY_PASSWORD_HASH, accountEntity.getPasswordHash())
        .addProperty(AccountEntity.PROPERTY_PASSWORD_SALT, accountEntity.getPasswordSalt())
        .addProperty(AccountEntity.PROPERTY_ROLE, accountEntity.getRole())
        .addProperty(AccountEntity.PROPERTY_USERNAME, accountEntity.getUsername());
  }

  /**
   * Creates a new account.
   *
   * @param account - contains new information for an account.
   * @return back an account object if creation was successful.
   * @throws InvalidRequestException if something went wrong with the request.
   */
  public Account createAccount(Account account) throws InvalidRequestException {
    try {
      var accountEntity = createNewAccountEntity(account);
      PasswordUtils.saltPassword(account, accountEntity);
      var entity = buildTableEntityFromAccountEntity(accountEntity);
      tableClient.createEntity(entity);
      return accountMapper.toAccount(accountEntity);
    } catch (TableServiceException e) {
      var statusCode = e.getResponse().getStatusCode();
      if (HttpStatus.BAD_REQUEST.value() == statusCode) {
        throw new RuntimeException("Failed to create account: " + e.getMessage(), e);
      } else if(HttpStatus.ALREADY_REPORTED.value() == statusCode) {
        throw new RuntimeException("Failed to create account: " + e.getMessage(), e);
      }
      throw new InvalidRequestException("Failed to create account: " + e.getMessage(), e);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new InvalidRequestException("Failed to create account due to password salting: " + e.getMessage(), e);
    }
  }

  /**
   * Creates a new default account entity using a given account object.
   *
   * @param account - the account to create the accountEntity based on.
   * @return a new account entity with default settings.
   */
  public AccountEntity createNewAccountEntity(Account account) {
    return AccountEntity.builder()
        .approvedBy(null)
        .createdOn(Instant.now())
        .email(account.getEmail())
        .firstName(account.getFirstName())
        .isApproved(false)
        .lastName(account.getLastName())
        .role(Role.USER)
        .username(account.getUsername())
        .build();
  }

  /**
   * Gets an account by its email address.
   *
   * @param rowKey the email address of the account.
   * @return the account with the given email address.
   * @throws ResourceNotFoundException if the account cannot be found.
   */
  public Account getAccount(String rowKey) throws ResourceNotFoundException {
    try {
      var entity = tableClient.getEntity(AccountEntity.PARTITION_KEY, rowKey);
      var accountEntity = TableEntityUtils.toAccountEntity(entity);
      return accountMapper.toAccount(accountEntity);
    } catch (TableServiceException e) {
      if (e.getResponse().getStatusCode() == 404) {
        throw new ResourceNotFoundException("can't find resource");
      }
      throw new RuntimeException("Failed to retrieve account: " + e.getMessage(), e);
    }
  }

  /**
   * List all accounts on in the system.
   *
   * @return a list of all accounts.
   */
  public List<Account> getAccounts() {

    var options = new ListEntitiesOptions()
        .setFilter("PartitionKey eq 'ACCOUNT'");
    var accountEntities = tableClient.listEntities(options, null, null);
    return accountEntities.stream()
        .map(TableEntityUtils::toAccountEntity)
        .map(accountMapper::toAccount)
        .toList();
  }

  /**
   * Validates login information from a request and returns a JWT if it is correct.
   *
   * @param account - account for which the requester wishes to gain access to.
   * @return a JWT token.
   * @throws InvalidTokenException - if login information is incorrect.
   */
  public String loginAccount(Account account) throws InvalidTokenException {

    try {
      var email = account.getEmail();
      var password = account.getPassword();

      var options = new ListEntitiesOptions().setFilter("email eq '" + email + "'");
      var tableEntityAccounts = tableClient.listEntities(options, null, null);
      var accountEntities = tableEntityAccounts.stream()
          .map(TableEntityUtils::toAccountEntity)
          .toList();

      var accountEntity = accountEntities.getFirst();
      var salt = accountEntity.getPasswordSalt();
      var hash = accountEntity.getPasswordHash();
      var isValidPassword = PasswordUtils.verifyPassword(password, salt, hash);

      if(isValidPassword) {
        PermissionService.isAccountApproved(accountEntity);
        return PermissionService.generateToken(accountEntity);
      } else {
        throw new InvalidTokenException("Given Login information was not correct.");
      }
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

}
