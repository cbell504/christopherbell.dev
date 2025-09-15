package dev.christopherbell.account;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.christopherbell.account.model.dto.AccountUpdateRequest;
import dev.christopherbell.account.model.dto.AccountDetail;
import dev.christopherbell.account.model.AccountStatus;
import dev.christopherbell.account.model.Role;
import dev.christopherbell.libs.api.APIVersion;
import dev.christopherbell.libs.api.controller.ControllerExceptionHandler;
import dev.christopherbell.libs.api.exception.InvalidRequestException;
import dev.christopherbell.libs.api.exception.ResourceExistsException;
import dev.christopherbell.libs.api.exception.ResourceNotFoundException;
import dev.christopherbell.libs.test.TestUtil;
import dev.christopherbell.permission.PermissionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
@Import(ControllerExceptionHandler.class)
public class AccountControllerTest {
  @Autowired private MockMvc mockMvc;
  @MockitoBean private PermissionService permissionService;
  @MockitoBean private AccountService accountService;

  @Test
  @DisplayName("Should update an account when caller has ADMIN role.")
  @WithMockUser(authorities = {"ADMIN"})
  public void testUpdateAccount() throws Exception {
    var request = TestUtil.readJsonAsString("/request/account-update-request.json");
    var requestObject = TestUtil.readJsonAsObject("/request/account-update-request.json", AccountUpdateRequest.class);

    var detail = AccountDetail.builder()
        .id(requestObject.id())
        .email(requestObject.email())
        .firstName(requestObject.firstName())
        .lastName(requestObject.lastName())
        .username(requestObject.username())
        .role(Role.ADMIN)
        .status(AccountStatus.ACTIVE)
        .build();

    when(accountService.updateAccount(eq(requestObject))).thenReturn(detail);

    mockMvc
        .perform(
            put("/api/accounts" + APIVersion.V20250914)
                .with(csrf())
                .content(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isAccepted())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.payload").isNotEmpty())
        .andExpect(jsonPath("$.payload.id").value(requestObject.id()))
        .andExpect(jsonPath("$.payload.username").value(requestObject.username()))
        .andExpect(jsonPath("$.payload.email").value(requestObject.email()))
        .andExpect(jsonPath("$.payload.firstName").value(requestObject.firstName()))
        .andExpect(jsonPath("$.payload.lastName").value(requestObject.lastName()));

    verify(accountService).updateAccount(eq(requestObject));
  }

  @Test
  @DisplayName("testUpdateAccount_whenNotAuthorized_Returns401")
  public void testUpdateAccount_whenNotAuthorized_Returns401() throws Exception {
    var request = TestUtil.readJsonAsString("/request/account-update-request.json");

    mockMvc
        .perform(
            put("/api/accounts" + APIVersion.V20250914)
                .with(csrf())
                .content(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(accountService);
  }

  @Test
  @DisplayName("testUpdateAccount_whenInvalidRequest_Returns400")
  @WithMockUser(authorities = {"ADMIN"})
  public void testUpdateAccount_whenInvalidRequest_Returns400() throws Exception {
    var request = TestUtil.readJsonAsString("/request/account-update-request.json");
    var requestObject = TestUtil.readJsonAsObject("/request/account-update-request.json", AccountUpdateRequest.class);

    when(accountService.updateAccount(eq(requestObject)))
        .thenThrow(new InvalidRequestException("Account id cannot be null or blank."));

    mockMvc
        .perform(
            put("/api/accounts" + APIVersion.V20250914)
                .with(csrf())
                .content(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest());

    verify(accountService).updateAccount(eq(requestObject));
  }

  @Test
  @DisplayName("testUpdateAccount_whenNotFound_Returns404")
  @WithMockUser(authorities = {"ADMIN"})
  public void testUpdateAccount_whenNotFound_Returns404() throws Exception {
    var request = TestUtil.readJsonAsString("/request/account-update-request.json");
    var requestObject = TestUtil.readJsonAsObject("/request/account-update-request.json", AccountUpdateRequest.class);

    when(accountService.updateAccount(eq(requestObject)))
        .thenThrow(new ResourceNotFoundException("Account not found"));

    mockMvc
        .perform(
            put("/api/accounts" + APIVersion.V20250914)
                .with(csrf())
                .content(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound());

    verify(accountService).updateAccount(eq(requestObject));
  }

  @Test
  @DisplayName("testUpdateAccount_whenWrongContentType_Returns415")
  @WithMockUser(authorities = {"ADMIN"})
  public void testUpdateAccount_whenWrongContentType_Returns415() throws Exception {
    var request = TestUtil.readJsonAsString("/request/account-update-request.json");

    mockMvc
        .perform(
            put("/api/accounts" + APIVersion.V20250914)
                .with(csrf())
                .content(request)
                .contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isUnsupportedMediaType());

    verifyNoInteractions(accountService);
  }

  @Test
  @DisplayName("testUpdateAccount_whenAcceptHeaderUnsupported_Returns406")
  @WithMockUser(authorities = {"ADMIN"})
  public void testUpdateAccount_whenAcceptHeaderUnsupported_Returns406() throws Exception {
    var request = TestUtil.readJsonAsString("/request/account-update-request.json");

    mockMvc
        .perform(
            put("/api/accounts" + APIVersion.V20250914)
                .with(csrf())
                .content(request)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN))
        .andExpect(status().isNotAcceptable());

    verifyNoInteractions(accountService);
  }

  @Test
  @DisplayName("testUpdateAccount_whenConflict_Returns409")
  @WithMockUser(authorities = {"ADMIN"})
  public void testUpdateAccount_whenConflict_Returns409() throws Exception {
    var request = TestUtil.readJsonAsString("/request/account-update-request.json");
    var requestObject = TestUtil.readJsonAsObject("/request/account-update-request.json", AccountUpdateRequest.class);

    when(accountService.updateAccount(eq(requestObject)))
        .thenThrow(new ResourceExistsException("Email already exists"));

    mockMvc
        .perform(
            put("/api/accounts" + APIVersion.V20250914)
                .with(csrf())
                .content(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isConflict());

    verify(accountService).updateAccount(eq(requestObject));
  }

  @Test
  @DisplayName("testApproveAccount_whenAuthorized_Returns200")
  @WithMockUser(authorities = {"ADMIN"})
  public void testApproveAccount_whenAuthorized_Returns200() throws Exception {
    var detail = AccountDetail.builder()
        .id("acc-42")
        .email("user@example.com")
        .username("user42")
        .build();

    when(accountService.approveAccount(eq("acc-42"))).thenReturn(detail);

    mockMvc
        .perform(
            post("/api/accounts" + APIVersion.V20250903 + "/approve/{accountId}", "acc-42")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.payload.id").value("acc-42"));

    verify(accountService).approveAccount(eq("acc-42"));
  }

  @Test
  @DisplayName("testDeleteAccount_whenAuthorized_Returns200")
  @WithMockUser(authorities = {"ADMIN"})
  public void testDeleteAccount_whenAuthorized_Returns200() throws Exception {
    var detail = AccountDetail.builder().id("to-del").build();
    when(accountService.deleteAccount(eq("to-del"))).thenReturn(detail);

    mockMvc
        .perform(
            delete("/api/accounts" + APIVersion.V20250903 + "/{id}", "to-del")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.payload.id").value("to-del"));

    verify(accountService).deleteAccount(eq("to-del"));
  }

  @Test
  @DisplayName("testGetAccountByEmail_whenAuthorized_Returns200")
  @WithMockUser(authorities = {"ADMIN"})
  public void testGetAccountByEmail_whenAuthorized_Returns200() throws Exception {
    var detail = AccountDetail.builder().id("acc-1").email("user@example.com").build();
    when(accountService.getAccountByEmail(eq("user@example.com"))).thenReturn(detail);

    mockMvc
        .perform(
            get("/api/accounts" + dev.christopherbell.libs.api.APIVersion.V20241215 + "/email/{email}", "user@example.com")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.payload.email").value("user@example.com"));

    verify(accountService).getAccountByEmail(eq("user@example.com"));
  }

  @Test
  @DisplayName("testGetMyAccount_whenUserAuthorized_Returns200")
  @WithMockUser(authorities = {"USER"})
  public void testGetMyAccount_whenUserAuthorized_Returns200() throws Exception {
    var detail = AccountDetail.builder().id("me").email("me@example.com").build();
    when(accountService.getSelfAccount()).thenReturn(detail);

    mockMvc
        .perform(
            get("/api/accounts" + APIVersion.V20250903 + "/me")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.payload.id").value("me"));
  }

  @Test
  @DisplayName("testLoginAccount_whenValid_Returns200WithToken")
  @WithMockUser
  public void testLoginAccount_whenValid_Returns200WithToken() throws Exception {
    when(accountService.loginAccount(eq(new dev.christopherbell.account.model.AccountLoginRequest("user@example.com", "pass"))))
        .thenReturn("jwt-token");

    var json = "{\"email\":\"user@example.com\",\"password\":\"pass\"}";

    mockMvc
        .perform(
            post("/api/accounts" + APIVersion.V20241215 + "/login")
                .with(csrf())
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.payload").value("jwt-token"));
  }
}

