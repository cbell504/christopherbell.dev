//package dev.christopherbell.thevoid.invite;
//
//import dev.christopherbell.libs.api.exception.ResourceNotFoundException;
//import dev.christopherbell.libs.api.exception.InvalidRequestException;
//import dev.christopherbell.libs.api.exception.InvalidTokenException;
//import dev.christopherbell.libs.api.util.APIValidationUtils;
//import dev.christopherbell.thevoid.common.VoidRequest;
//import dev.christopherbell.thevoid.common.VoidResponse;
//import dev.christopherbell.thevoid.account.model.dto.Account;
//import dev.christopherbell.thevoid.account.VoidAccountMessenger;
//import dev.christopherbell.thevoid.invite.model.InviteCodeEntity;
//import dev.christopherbell.thevoid.permission.PermissionsService;
//import dev.christopherbell.libs.api.util.ValidateUtil;
//import dev.christopherbell.thevoid.utils.mappers.MapStructMapper;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.Objects;
//import java.util.UUID;
//
//@AllArgsConstructor
//@Service
//@Slf4j
//public class InviteCodeService {
//
//  private final VoidAccountMessenger voidAccountMessenger;
//  private final InviteCodeMessenger inviteCodeMessenger;
//  private final MapStructMapper mapStructMapper;
//  private final PermissionsService permissionsService;
//
//  /**
//   *
//   * @param clientId
//   * @param loginToken
//   * @param voidRequest
//   * @return
//   * @throws InvalidRequestException
//   * @throws ResourceNotFoundException
//   * @throws InvalidTokenException
//   */
//  public VoidResponse generateInviteCode(String clientId, String loginToken, VoidRequest voidRequest)
//      throws InvalidRequestException, ResourceNotFoundException, InvalidTokenException {
//    ValidateUtil.validateAccount(voidRequest);
//    APIValidationUtils.isValidClientId(ValidateUtil.ACCEPTED_CLIENT_IDs, clientId);
//
//    var account = Objects.requireNonNullElse(voidRequest.getAccount(), new Account());
//    var accountId = Objects.requireNonNullElse(account.getId(), 0L);
//
//    var isValidCreds = this.permissionsService.validateLoginToken(loginToken, accountId);
//    if (!isValidCreds) {
//      throw new InvalidTokenException("Invalid token.");
//    } else {
//      // Build a template success response
//      var accountEntity = this.voidAccountMessenger.getAccountEntityById(accountId);
//      var inviteCodeEntity = new InviteCodeEntity();
//      inviteCodeEntity.setAccountEntity(accountEntity);
//      inviteCodeEntity.setCode(UUID.randomUUID().toString());
//      // Save the invite Code
//      this.inviteCodeMessenger.saveInviteCode(inviteCodeEntity);
//
//      return VoidResponse.builder()
//          .inviteCode(mapStructMapper.mapToInviteCode(inviteCodeEntity))
//          .build();
//    }
//  }
//}
