package dev.christopherbell.account;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableServiceException;
import dev.christopherbell.account.model.dto.Account;
import dev.christopherbell.account.model.entity.AccountEntity;
import dev.christopherbell.account.model.dto.Role;
import dev.christopherbell.libs.api.exception.InvalidRequestException;
import dev.christopherbell.libs.api.exception.InvalidTokenException;
import dev.christopherbell.libs.api.exception.ResourceNotFoundException;
import dev.christopherbell.libs.security.PasswordUtil;
import dev.christopherbell.libs.security.PermissionService;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Represents the service responsible for handling getting, creating, updating, and deleting
 * accounts.
 */
@AllArgsConstructor
@Slf4j
@Service
public class AccountService {

  private final AccountMapper accountMapper;
  private final PermissionService permissionService;
  private final TableClient tableClient;

  /**
   * Creates a new account.
   *
   * @param account - contains new information for an account.
   * @return back an account object if creation was successful.
   * @throws InvalidRequestException if something went wrong with the request.
   */
  public Account createAccount(Account account) throws InvalidRequestException {
    try {
      String normalizedEmail = account.getEmail().toLowerCase();

      // Check for existing email
      var options = new ListEntitiesOptions().setFilter("email eq '" + normalizedEmail + "'");
      var existing = tableClient.listEntities(options, null, null);
      if (existing.iterator().hasNext()) {
        throw new InvalidRequestException("Account with this email already exists");
      }

      var accountEntity = createNewAccountEntity(account);
      accountEntity.setRowKey(UUID.randomUUID().toString());
      PasswordUtil.saltPassword(account, accountEntity);
      var tableEntity = accountEntity.toTableEntity();
      tableClient.createEntity(tableEntity);
      return accountMapper.toAccount(accountEntity);
    } catch (TableServiceException e) {
      handleTableServiceException(e, "create account");
      throw new InvalidRequestException("Failed to create account: " + e.getMessage(), e);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new InvalidRequestException("Password salting failed: " + e.getMessage(), e);
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

  public Account deleteAccount(Account account) throws ResourceNotFoundException {
    try {
      var entity = tableClient.getEntity(AccountEntity.PARTITION_KEY, account.getRowKey());
      tableClient.deleteEntity(entity.getPartitionKey(), entity.getRowKey());
      return accountMapper.toAccount(TableEntityUtils.toAccountEntity(entity));
    } catch (TableServiceException e) {
      if (e.getResponse().getStatusCode() == 404) {
        throw new ResourceNotFoundException("Account not found");
      }
      throw new RuntimeException("Failed to delete account", e);
    }
  }

  /**
   * Gets an account by its email address.
   *
   * @param email the email address of the account.
   * @return the account with the given email address.
   * @throws ResourceNotFoundException if the account cannot be found.
   */
  public Account getAccountByEmail(String email) throws ResourceNotFoundException {
    String normalizedEmail = email.toLowerCase();
    var options = new ListEntitiesOptions().setFilter("email eq '" + normalizedEmail + "'");
    var entities = tableClient.listEntities(options, null, null);
    var iterator = entities.iterator();
    if (!iterator.hasNext()) {
      throw new ResourceNotFoundException("Account not found by email");
    }
    var accountEntity = TableEntityUtils.toAccountEntity(iterator.next());
    return accountMapper.toAccount(accountEntity);
  }

  /**
   * Gets an account by its id
   *
   * @param rowKey the id of the account.
   * @return the account with the given id.
   * @throws ResourceNotFoundException if the account cannot be found.
   */
  public Account getAccountById(String rowKey) throws ResourceNotFoundException {
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

    var options =
        new ListEntitiesOptions()
            .setFilter("PartitionKey eq '" + AccountEntity.PARTITION_KEY + "'");
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
    String normalizedEmail = account.getEmail().toLowerCase();
    var options = new ListEntitiesOptions().setFilter("email eq '" + normalizedEmail + "'");
    var entities = tableClient.listEntities(options, null, null);
    var iterator = entities.iterator();

    if (!iterator.hasNext()) {
      throw new InvalidTokenException("Invalid login credentials");
    }

    var accountEntity = TableEntityUtils.toAccountEntity(iterator.next());
    try {
      var isValidPassword =
          PasswordUtil.verifyPassword(
              account.getPassword(),
              accountEntity.getPasswordSalt(),
              accountEntity.getPasswordHash());
      if (isValidPassword) {
        permissionService.isAccountApproved(accountEntity);
        return permissionService.generateToken(accountEntity);
      } else {
        throw new InvalidTokenException("Invalid password");
      }
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException("Password verification failed", e);
    }
  }

  private void handleTableServiceException(TableServiceException e, String action)
      throws InvalidRequestException {
    int code = e.getResponse().getStatusCode();
    if (code == 400) {
      throw new InvalidRequestException("Bad request during " + action + ": " + e.getMessage(), e);
    } else if (code == 409) {
      throw new InvalidRequestException("Conflict during " + action + ": " + e.getMessage(), e);
    }
  }
}
