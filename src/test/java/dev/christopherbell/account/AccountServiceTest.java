package dev.christopherbell.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.azure.data.tables.TableClient;
import dev.christopherbell.account.models.AccountEntity;
import dev.christopherbell.account.models.Role;
import dev.christopherbell.configuration.ApiUtilProperties;
import dev.christopherbell.libs.common.api.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

  private AccountService accountService;
  private AccountMapper accountMapper;
  @Mock
  private TableClient tableClient;

  @BeforeEach
  public void init() {
    accountMapper = new AccountMapperImpl();
    accountService = new AccountService(accountMapper, tableClient);
  }

  @Test
  public void buildTableEntityFromAccountEntity() {

    var accountEntity = AccountStub.getAccountEntityStub();
    var tableEntity = accountService.buildTableEntityFromAccountEntity(accountEntity);

    var tableEntityApprovedBy = tableEntity.getProperty(AccountEntity.PROPERTY_APPROVED_BY);
    var tableEntityCreatedOn = tableEntity.getProperty(AccountEntity.PROPERTY_CREATED_ON);
    var tableEntityEmail = tableEntity.getRowKey();
    var tableEntityFirstName = tableEntity.getProperty(AccountEntity.PROPERTY_FIRST_NAME);
    var tableEntityIsApproved = tableEntity.getProperty(AccountEntity.PROPERTY_IS_APPROVED);
    var tableEntityLastName = tableEntity.getProperty(AccountEntity.PROPERTY_LAST_NAME);
    var tableEntityRole = tableEntity.getProperty(AccountEntity.PROPERTY_ROLE);
    var tableEntityUsername = tableEntity.getProperty(AccountEntity.PROPERTY_USERNAME);

    assertEquals(accountEntity.getApprovedBy(), tableEntityApprovedBy);
    Assertions.assertNotNull(tableEntityCreatedOn);
    assertEquals(accountEntity.getEmail(), tableEntityEmail);
    assertEquals(accountEntity.getFirstName(), tableEntityFirstName);
    assertEquals(accountEntity.getIsApproved(), tableEntityIsApproved);
    assertEquals(accountEntity.getLastName(), tableEntityLastName);
    assertEquals(accountEntity.getRole(), tableEntityRole);
    assertEquals(accountEntity.getUsername(), tableEntityUsername);
  }

  @Test
  public void createAccount_success() throws InvalidRequestException {

    var account = AccountStub.getAccountStub();

    doNothing().when(tableClient).createEntity(any());
    var result = accountService.createAccount(account);

    assertNotNull(result.getCreatedOn());
    assertEquals(account.getEmail(), result.getEmail());
    assertEquals(account.getFirstName(), result.getFirstName());
    assertEquals(account.getLastName(), result.getLastName());
    assertEquals(account.getUsername(), result.getUsername());
  }

  @Test
  public void createNewAccountEntity() {

    var account = AccountStub.getAccountStub();
    var accountEntity = accountService.createNewAccountEntity(account);

    assertEquals(account.getApprovedBy(), accountEntity.getApprovedBy());
    Assertions.assertNotNull(accountEntity.getCreatedOn());
    assertEquals(account.getEmail(), accountEntity.getEmail());
    assertEquals(account.getFirstName(), accountEntity.getFirstName());
    Assertions.assertFalse(accountEntity.getIsApproved());
    assertEquals(account.getLastName(), accountEntity.getLastName());
    assertEquals(Role.USER, accountEntity.getRole());
    assertEquals(account.getUsername(), accountEntity.getUsername());
  }

}
