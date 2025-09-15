package dev.christopherbell.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import dev.christopherbell.account.model.Account;
import dev.christopherbell.account.model.AccountStatus;
import dev.christopherbell.account.model.Role;
import dev.christopherbell.account.model.dto.AccountDetail;
import dev.christopherbell.account.model.dto.AccountUpdateRequest;
import dev.christopherbell.libs.api.exception.InvalidRequestException;
import dev.christopherbell.libs.api.exception.ResourceExistsException;
import dev.christopherbell.libs.api.exception.ResourceNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
  @Mock private AccountMapper accountMapper;
  @Mock private AccountRepository accountRepository;
  @InjectMocks private AccountService accountService;

  @Test
  @DisplayName("Update: null request -> 400 InvalidRequestException")
  public void testUpdateAccount_whenNullRequest_throwsInvalidRequestException() {
    assertThrows(InvalidRequestException.class, () -> accountService.updateAccount(null));
  }

  @Test
  @DisplayName("GetByEmail: found -> returns mapped detail")
  public void testGetAccountByEmail_whenFound_ReturnsDetail() throws Exception {
    var entity = AccountServiceStub.getAccountWhenExistsStub();
    var detail = AccountDetail.builder().id(entity.getId()).email(entity.getEmail()).build();

    when(accountRepository.findByEmail(eq("old@example.com")))
        .thenReturn(Optional.of(entity));
    when(accountMapper.toAccount(eq(entity))).thenReturn(detail);

    var result = accountService.getAccountByEmail("old@example.com");

    assertEquals(detail, result);
    verify(accountRepository).findByEmail(eq("old@example.com"));
    verify(accountMapper).toAccount(eq(entity));
    verifyNoMoreInteractions(accountRepository, accountMapper);
  }

  @Test
  @DisplayName("GetByEmail: not found -> throws 404")
  public void testGetAccountByEmail_whenNotFound_Throws404() {
    when(accountRepository.findByEmail(eq("missing@example.com")))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountByEmail("missing@example.com"));
    verify(accountRepository).findByEmail(eq("missing@example.com"));
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("GetByUsername: found -> returns mapped detail")
  public void testGetAccountByUsername_whenFound_ReturnsDetail() throws Exception {
    var entity = AccountServiceStub.getAccountWhenExistsStub();
    var detail = AccountDetail.builder().id(entity.getId()).username(entity.getUsername()).build();

    when(accountRepository.findByUsername(eq("old_user")))
        .thenReturn(Optional.of(entity));
    when(accountMapper.toAccount(eq(entity))).thenReturn(detail);

    var result = accountService.getAccountByUsername("old_user");

    assertEquals(detail, result);
    verify(accountRepository).findByUsername(eq("old_user"));
    verify(accountMapper).toAccount(eq(entity));
    verifyNoMoreInteractions(accountRepository, accountMapper);
  }

  @Test
  @DisplayName("GetByUsername: not found -> throws 404")
  public void testGetAccountByUsername_whenNotFound_Throws404() {
    when(accountRepository.findByUsername(eq("missing_user")))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountByUsername("missing_user"));
    verify(accountRepository).findByUsername(eq("missing_user"));
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("GetById: found -> returns mapped detail")
  public void testGetAccountById_whenFound_ReturnsDetail() throws Exception {
    var entity = AccountServiceStub.getAccountWhenExistsStub();
    var detail = AccountDetail.builder().id(entity.getId()).build();

    when(accountRepository.findById(eq(AccountServiceStub.ID)))
        .thenReturn(Optional.of(entity));
    when(accountMapper.toAccount(eq(entity))).thenReturn(detail);

    var result = accountService.getAccountById(AccountServiceStub.ID);

    assertEquals(detail, result);
    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verify(accountMapper).toAccount(eq(entity));
    verifyNoMoreInteractions(accountRepository, accountMapper);
  }

  @Test
  @DisplayName("Delete: found -> deletes and returns mapped detail")
  public void testDeleteAccount_whenFound_DeletesAndReturnsDetail() throws Exception {
    var entity = AccountServiceStub.getAccountWhenExistsStub();
    var detail = AccountDetail.builder().id(entity.getId()).build();

    when(accountRepository.findById(eq(AccountServiceStub.ID)))
        .thenReturn(Optional.of(entity));
    when(accountMapper.toAccount(eq(entity))).thenReturn(detail);

    var result = accountService.deleteAccount(AccountServiceStub.ID);

    assertEquals(detail, result);
    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verify(accountRepository).delete(eq(entity));
    verify(accountMapper).toAccount(eq(entity));
    verifyNoMoreInteractions(accountRepository, accountMapper);
  }

  @Test
  @DisplayName("Delete: not found -> throws 404")
  public void testDeleteAccount_whenNotFound_Throws404() {
    when(accountRepository.findById(eq(AccountServiceStub.ID)))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> accountService.deleteAccount(AccountServiceStub.ID));
    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Approve: found -> sets flags, saves, returns mapped detail")
  public void testApproveAccount_whenFound_ApprovesAndReturnsDetail() throws Exception {
    var entity = AccountServiceStub.getAccountWhenExistsStub();
    var approved = AccountDetail.builder().id(entity.getId()).build();
    var service = spy(new AccountService(accountMapper, accountRepository));

    doReturn(AccountDetail.builder().id("self-1").build()).when(service).getSelfAccount();
    when(accountRepository.findById(eq(AccountServiceStub.ID))).thenReturn(Optional.of(entity));
    when(accountRepository.save(eq(entity))).thenReturn(entity);
    when(accountMapper.toAccount(eq(entity))).thenReturn(approved);

    var result = service.approveAccount(AccountServiceStub.ID);

    assertEquals(approved, result);
    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verify(accountRepository).save(eq(entity));
    verify(accountMapper).toAccount(eq(entity));
  }

  @Test
  @DisplayName("Approve: not found -> throws 404")
  public void testApproveAccount_whenNotFound_Throws404() {
    when(accountRepository.findById(eq(AccountServiceStub.ID))).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> accountService.approveAccount(AccountServiceStub.ID));

    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Update: blank id -> 400 InvalidRequestException")
  public void testUpdateAccount_whenBlankId_throwsInvalidRequestException() {
    var request = AccountServiceStub.getAccountUpdateRequestWhenBlankIdStub();
    assertThrows(InvalidRequestException.class, () -> accountService.updateAccount(request));
  }

  @Test
  @DisplayName("Update: not found -> 404 ResourceNotFoundException")
  public void testUpdateAccount_whenNotFound_throwsResourceNotFoundException() {
    var request = AccountUpdateRequest.builder().id(AccountServiceStub.ID).build();

    when(accountRepository.findById(eq(AccountServiceStub.ID)))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> accountService.updateAccount(request));

    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Update: all fields set -> applies changes, sanitizes, returns detail")
  public void testUpdateAccount_whenValid_appliesChangesAndReturnsDetail() throws Exception {
    var existing = AccountServiceStub.getAccountWhenExistsStub();
    var request = AccountServiceStub.getAccountUpdateRequestWhenAllFieldsSetStub();

    when(accountRepository.findById(eq(AccountServiceStub.ID)))
        .thenReturn(Optional.of(existing));
    // No conflicts for new email/username
    when(accountRepository.findByEmail(eq("chris@example.com")))
        .thenReturn(Optional.empty());
    when(accountRepository.findByUsername(eq("Chris.Bell")))
        .thenReturn(Optional.empty());
    when(accountRepository.save(eq(existing))).thenReturn(existing);

    var detail = AccountDetail.builder()
        .id(AccountServiceStub.ID)
        .email("chris@example.com")
        .firstName("Chris")
        .lastName("Bell")
        .username("Chris.Bell")
        .role(Role.ADMIN)
        .status(AccountStatus.ACTIVE)
        .isApproved(true)
        .build();
    when(accountMapper.toAccount(eq(existing))).thenReturn(detail);

    AccountDetail result = accountService.updateAccount(request);

    assertNotNull(result);
    assertEquals(AccountServiceStub.ID, result.getId());
    assertEquals("chris@example.com", result.getEmail());
    assertEquals("Chris", result.getFirstName());
    assertEquals("Bell", result.getLastName());
    assertEquals("Chris.Bell", result.getUsername());
    assertEquals(Role.ADMIN, result.getRole());
    assertEquals(AccountStatus.ACTIVE, result.getStatus());

    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verify(accountRepository).findByEmail(eq("chris@example.com"));
    verify(accountRepository).findByUsername(eq("Chris.Bell"));
    verify(accountRepository).save(eq(existing));
    verify(accountMapper).toAccount(eq(existing));
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Update: role only -> updates role, keeps others unchanged")
  public void testUpdateAccount_whenRoleOnly_updatesRoleAndKeepsOthers() throws Exception {
    var existing = AccountServiceStub.getAccountWhenExistsStub();
    var request = AccountServiceStub.getAccountUpdateRequestWhenRoleOnlyStub();

    when(accountRepository.findById(eq(AccountServiceStub.ID)))
        .thenReturn(Optional.of(existing));
    when(accountRepository.save(eq(existing))).thenReturn(existing);

    var detail = AccountDetail.builder()
        .id(AccountServiceStub.ID)
        .email(existing.getEmail())
        .firstName(existing.getFirstName())
        .lastName(existing.getLastName())
        .username(existing.getUsername())
        .role(Role.ADMIN)
        .status(existing.getStatus())
        .isApproved(existing.getIsApproved())
        .build();
    when(accountMapper.toAccount(eq(existing))).thenReturn(detail);

    AccountDetail result = accountService.updateAccount(request);

    assertEquals(Role.ADMIN, result.getRole());
    assertEquals(existing.getEmail(), result.getEmail());
    assertEquals(existing.getUsername(), result.getUsername());
    assertEquals(existing.getFirstName(), result.getFirstName());
    assertEquals(existing.getLastName(), result.getLastName());
    assertEquals(existing.getStatus(), result.getStatus());
    assertEquals(existing.getIsApproved(), result.getIsApproved());

    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verify(accountRepository).save(eq(existing));
    verify(accountMapper).toAccount(eq(existing));
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Update: flags only -> updates status and approval")
  public void testUpdateAccount_whenFlagsOnly_updatesStatusAndApproval() throws Exception {
    var existing = AccountServiceStub.getAccountWhenExistsStub();
    var request = AccountServiceStub.getAccountUpdateRequestWhenFlagsOnlyStub();

    when(accountRepository.findById(eq(AccountServiceStub.ID)))
        .thenReturn(Optional.of(existing));
    when(accountRepository.save(eq(existing))).thenReturn(existing);

    var detail = AccountDetail.builder()
        .id(AccountServiceStub.ID)
        .email(existing.getEmail())
        .firstName(existing.getFirstName())
        .lastName(existing.getLastName())
        .username(existing.getUsername())
        .role(existing.getRole())
        .status(AccountStatus.ACTIVE)
        .isApproved(true)
        .build();
    when(accountMapper.toAccount(eq(existing))).thenReturn(detail);

    AccountDetail result = accountService.updateAccount(request);

    assertEquals(AccountStatus.ACTIVE, result.getStatus());
    assertEquals(true, result.getIsApproved());

    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verify(accountRepository).save(eq(existing));
    verify(accountMapper).toAccount(eq(existing));
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Update: only id -> keeps all values unchanged")
  public void testUpdateAccount_whenOnlyId_keepsExistingValues() throws Exception {
    var existing = AccountServiceStub.getAccountWhenExistsStub();
    var request = AccountServiceStub.getAccountUpdateRequestWhenOnlyIdStub();

    when(accountRepository.findById(eq(AccountServiceStub.ID)))
        .thenReturn(Optional.of(existing));
    when(accountRepository.save(eq(existing))).thenReturn(existing);

    var detail = AccountDetail.builder()
        .id(AccountServiceStub.ID)
        .email(existing.getEmail())
        .firstName(existing.getFirstName())
        .lastName(existing.getLastName())
        .username(existing.getUsername())
        .role(existing.getRole())
        .status(existing.getStatus())
        .isApproved(existing.getIsApproved())
        .build();
    when(accountMapper.toAccount(eq(existing))).thenReturn(detail);

    AccountDetail result = accountService.updateAccount(request);

    assertEquals(existing.getEmail(), result.getEmail());
    assertEquals(existing.getUsername(), result.getUsername());
    assertEquals(existing.getFirstName(), result.getFirstName());
    assertEquals(existing.getLastName(), result.getLastName());
    assertEquals(existing.getRole(), result.getRole());
    assertEquals(existing.getStatus(), result.getStatus());
    assertEquals(existing.getIsApproved(), result.getIsApproved());

    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verify(accountRepository).save(eq(existing));
    verify(accountMapper).toAccount(eq(existing));
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Update: invalid email -> throws IllegalArgumentException")
  public void testUpdateAccount_whenInvalidEmail_throwsIllegalArgumentException() {
    var existing = AccountServiceStub.getAccountWhenExistsStub();
    var request = AccountServiceStub.getAccountUpdateRequestWhenInvalidEmailStub();

    when(accountRepository.findById(eq(AccountServiceStub.ID)))
        .thenReturn(Optional.of(existing));

    assertThrows(IllegalArgumentException.class, () -> accountService.updateAccount(request));

    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Update: invalid username -> throws IllegalArgumentException")
  public void testUpdateAccount_whenInvalidUsername_throwsIllegalArgumentException() {
    var existing = AccountServiceStub.getAccountWhenExistsStub();
    var request = AccountServiceStub.getAccountUpdateRequestWhenInvalidUsernameStub();

    when(accountRepository.findById(eq(AccountServiceStub.ID)))
        .thenReturn(Optional.of(existing));

    assertThrows(IllegalArgumentException.class, () -> accountService.updateAccount(request));

    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Update: email exists -> throws ResourceExistsException and does not save")
  public void testUpdateAccount_whenEmailExists_throwsResourceExistsException() {
    var existing = AccountServiceStub.getAccountWhenExistsStub();
    var request = AccountServiceStub.getAccountUpdateRequestWhenAllFieldsSetStub();

    // Another account already owns the sanitized target email
    var other = Account.builder()
        .id("acc-999")
        .email("chris@example.com")
        .username("someoneElse")
        .build();

    when(accountRepository.findById(eq(AccountServiceStub.ID)))
        .thenReturn(Optional.of(existing));
    when(accountRepository.findByEmail(eq("chris@example.com")))
        .thenReturn(Optional.of(other));

    assertThrows(ResourceExistsException.class, () -> accountService.updateAccount(request));

    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verify(accountRepository).findByEmail(eq("chris@example.com"));
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  @DisplayName("Update: username exists -> throws ResourceExistsException and does not save")
  public void testUpdateAccount_whenUsernameExists_throwsResourceExistsException() {
    var existing = AccountServiceStub.getAccountWhenExistsStub();
    var request = AccountServiceStub.getAccountUpdateRequestWhenAllFieldsSetStub();

    // Email is available, but username is taken by another account
    var other = Account.builder()
        .id("acc-888")
        .email("someone@example.com")
        .username("Chris.Bell")
        .build();

    when(accountRepository.findById(eq(AccountServiceStub.ID)))
        .thenReturn(Optional.of(existing));
    when(accountRepository.findByEmail(eq("chris@example.com")))
        .thenReturn(Optional.empty());
    when(accountRepository.findByUsername(eq("Chris.Bell")))
        .thenReturn(Optional.of(other));

    assertThrows(ResourceExistsException.class, () -> accountService.updateAccount(request));

    verify(accountRepository).findById(eq(AccountServiceStub.ID));
    verify(accountRepository).findByEmail(eq("chris@example.com"));
    verify(accountRepository).findByUsername(eq("Chris.Bell"));
    verifyNoMoreInteractions(accountRepository);
  }
}
