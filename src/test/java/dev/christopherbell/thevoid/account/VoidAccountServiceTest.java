package dev.christopherbell.thevoid.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import dev.christopherbell.libs.common.api.exceptions.InvalidRequestException;
import dev.christopherbell.libs.common.api.exceptions.ResourceExistsException;
import dev.christopherbell.libs.common.api.utils.APIConstants;
import dev.christopherbell.thevoid.common.VoidRequest;
import dev.christopherbell.thevoid.account.model.entity.AccountEntity;
import dev.christopherbell.thevoid.account.model.dto.Account;
import dev.christopherbell.thevoid.permission.PermissionsService;
import dev.christopherbell.thevoid.testutils.AccountStub;
import dev.christopherbell.thevoid.utils.mappers.MapStructMapper;
import dev.christopherbell.thevoid.utils.mappers.MapStructMapperImpl;
import java.util.ArrayList;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class VoidAccountServiceTest {
  @InjectMocks
  private VoidAccountService voidAccountService;
  @Mock
  private VoidAccountMessenger voidAccountMessenger;
  @Mock
  private VoidAccountRepository voidAccountRepository;

  private MapStructMapper mapStructMapper;
  @Mock
  private PermissionsService permissionsService;

  @BeforeEach
  public void init() {
    mapStructMapper = new MapStructMapperImpl();
    voidAccountService = new VoidAccountService(voidAccountMessenger, voidAccountRepository, mapStructMapper, permissionsService);
  }

  @Test
  public void createAccountTest_Failure_badClientId() {
   var request = VoidRequest.builder()
       .account(AccountStub.getAccount())
       .build();
   var clientId = "void-api";

    var exception = assertThrows(
        InvalidRequestException.class,
        () -> voidAccountService.createAccount(clientId, request)
    );

    assertTrue(exception.getMessage().contains(APIConstants.VALIDATION_BAD_CLIENT_ID));
  }

  @Test
  public void createAccountTest_Failure_requestIsNull() {
    VoidRequest request = null;
    var clientId = AccountStub.getClientId();

    var exception = assertThrows(
        InvalidRequestException.class,
        () -> voidAccountService.createAccount(clientId, request)
    );

    assertTrue(exception.getMessage().contains("The request is null"));
  }

  @Test
  public void createAccountTest_Failure_requestHasNoAccountInfo() {
    VoidRequest request = VoidRequest.builder()
        .account(null)
        .build();
    var clientId = AccountStub.getClientId();

    var exception = assertThrows(
        InvalidRequestException.class,
        () -> voidAccountService.createAccount(clientId, request)
    );

    assertTrue(exception.getMessage().contains("The request contains no account information"));
  }

  @Test
  public void createAccountTest_Failure_requestHasBlankUserName() {
    VoidRequest request = VoidRequest.builder()
        .account(Account.builder()
            .username("")
            .build())
        .build();
    var clientId = AccountStub.getClientId();

    var exception = assertThrows(
        InvalidRequestException.class,
        () -> voidAccountService.createAccount(clientId, request)
    );

    assertTrue(exception.getMessage().contains("The given username is not valid"));
  }

  @Test
  public void createAccountTest_Failure_accountWithUserExists() {
    VoidRequest request = VoidRequest.builder()
        .account(AccountStub.getAccount())
        .build();
    var clientId = AccountStub.getClientId();

    when(voidAccountRepository.findByUsername(anyString())).thenReturn(Optional.of(new AccountEntity()));

    var exception = assertThrows(
        ResourceExistsException.class,
        () -> voidAccountService.createAccount(clientId, request)
    );

    assertTrue(exception.getMessage().contains("Account with this username already exists"));
  }

  @Test
  public void getAccounts_success() throws InvalidRequestException {
    var accountEntities = new ArrayList<AccountEntity>();
    accountEntities.add(AccountStub.getAccountEntity());
    accountEntities.add(AccountStub.getAccountEntity());
    accountEntities.add(AccountStub.getAccountEntity());
    accountEntities.add(AccountStub.getAccountEntity());

    when(voidAccountMessenger.getAccountEntities()).thenReturn(accountEntities);

    var accountsResponse = voidAccountService.getAccounts(AccountStub.getClientId());
    var accounts = accountsResponse.getAccounts();
    var firstActualAccount = accounts.getFirst();
    var firstExpectedAccount = accountEntities.getFirst();

    assertEquals(accounts.size(), 4);
    assertEquals(firstActualAccount.getId(), firstExpectedAccount.getId());
    assertEquals(firstActualAccount.getUsername(), firstExpectedAccount.getUsername());
    assertEquals(firstActualAccount.getCries().size(), firstExpectedAccount.getCries().size());

  }
}
