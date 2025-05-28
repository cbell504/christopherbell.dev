package dev.christopherbell.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import com.azure.data.tables.TableClient;
import dev.christopherbell.account.model.entity.AccountEntity;
import dev.christopherbell.account.model.dto.Role;
import dev.christopherbell.libs.api.exception.InvalidRequestException;
import dev.christopherbell.libs.security.PermissionService;
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
  private PermissionService permissionService;
  @Mock
  private TableClient tableClient;

  @BeforeEach
  public void init() {
    accountMapper = new AccountMapperImpl();
    accountService = new AccountService(accountMapper, permissionService, tableClient);
  }

//  @Test
//  public void createAccount_success() throws InvalidRequestException {
//
//    var account = AccountStub.getAccountStub();
//
//    doNothing().when(tableClient).createEntity(any());
//    var result = accountService.createAccount(account);
//
//    assertNotNull(result.getCreatedOn());
//    assertEquals(account.getEmail(), result.getEmail());
//    assertEquals(account.getFirstName(), result.getFirstName());
//    assertEquals(account.getLastName(), result.getLastName());
//    assertEquals(account.getUsername(), result.getUsername());
//  }

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
