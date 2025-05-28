//package dev.christopherbell.account;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import dev.christopherbell.account.model.dto.Account;
//import dev.christopherbell.libs.api.exception.InvalidRequestException;
//import dev.christopherbell.libs.api.exception.InvalidTokenException;
//import dev.christopherbell.libs.api.exception.ResourceNotFoundException;
//import dev.christopherbell.libs.security.PermissionService;
//import dev.christopherbell.libs.security.RateLimiter;
//import java.util.List;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@WebMvcTest(AccountController.class)
//public class AccountControllerTest {
//
//  private static final String BASE_PATH = "/api/accounts/20241215";
//
//  @Autowired
//  private MockMvc mockMvc;
//
//  @MockitoBean
//  private AccountService accountService;
//
//  @MockitoBean
//  private PermissionService permissionService;
//
//  @MockitoBean
//  private RateLimiter rateLimiter;
//
//  @Autowired
//  private ObjectMapper objectMapper;
//
//  @Nested
//  @DisplayName("POST /createAccount")
//  public class CreateAccountTests {
//
//    @Test
//    public void shouldCreateAccount() throws Exception {
//      Account account = new Account(); // populate fields
//      when(accountService.createAccount(any())).thenReturn(account);
//
//      mockMvc.perform(post(BASE_PATH)
//              .contentType(MediaType.APPLICATION_JSON)
//              .content(objectMapper.writeValueAsString(account)))
//          .andExpect(status().isCreated())
//          .andExpect(jsonPath("$.success").value(true));
//    }
//
//    @Test
//    public  void shouldReturnInvalidRequestException() throws Exception {
//      when(accountService.createAccount(any())).thenThrow(new InvalidRequestException("Invalid"));
//
//      mockMvc.perform(post(BASE_PATH)
//              .contentType(MediaType.APPLICATION_JSON)
//              .content(objectMapper.writeValueAsString(new Account())))
//          .andExpect(status().isInternalServerError());
//    }
//  }
//
//  @Nested
//  @DisplayName("GET /getAccountByEmail")
//  public class GetAccountByEmailTests {
//    @Test
//    @WithMockUser
//    void shouldGetAccount() throws Exception {
//      when(permissionService.hasAuthority("ADMIN")).thenReturn(true);
//      Account account = new Account(); // populate fields
//      when(accountService.getAccount(any())).thenReturn(account);
//
//      mockMvc.perform(get(BASE_PATH + "/email@example.com"))
//          .andExpect(status().isOk())
//          .andExpect(jsonPath("$.success").value(true));
//    }
//
//    @Test
//    @WithMockUser
//    public void shouldReturnNotFound() throws Exception {
//      when(permissionService.hasAuthority("ADMIN")).thenReturn(true);
//      when(accountService.getAccount(any())).thenThrow(new ResourceNotFoundException("Not found"));
//
//      mockMvc.perform(get(BASE_PATH + "/missing@example.com"))
//          .andExpect(status().isInternalServerError());
//    }
//  }
//
//  @Nested
//  @DisplayName("GET /getAccounts")
//  public class GetAccountsTests {
//    @Test
//    @WithMockUser
//    void shouldGetAllAccounts() throws Exception {
//      when(permissionService.hasAuthority("ADMIN")).thenReturn(true);
//      when(accountService.getAccounts()).thenReturn(List.of(new Account()));
//
//      mockMvc.perform(get(BASE_PATH))
//          .andExpect(status().isOk())
//          .andExpect(jsonPath("$.success").value(true));
//    }
//  }
//
//  @Nested
//  @DisplayName("POST /authenticate")
//  public class AuthenticateTests {
//    @Test
//    void shouldLoginAccount() throws Exception {
//      Account account = new Account(); // populate fields
//      when(accountService.loginAccount(any())).thenReturn("token");
//
//      mockMvc.perform(post(BASE_PATH + "/authenticate")
//              .contentType(MediaType.APPLICATION_JSON)
//              .content(objectMapper.writeValueAsString(account)))
//          .andExpect(status().isOk())
//          .andExpect(jsonPath("$.success").value(true));
//    }
//
//    @Test
//    public void shouldReturnInvalidToken() throws Exception {
//      when(accountService.loginAccount(any())).thenThrow(new InvalidTokenException("Invalid"));
//
//      mockMvc.perform(post(BASE_PATH + "/authenticate")
//              .contentType(MediaType.APPLICATION_JSON)
//              .content(objectMapper.writeValueAsString(new Account())))
//          .andExpect(status().isInternalServerError());
//    }
//  }
//}
