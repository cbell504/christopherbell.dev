//package dev.christopherbell.thevoid.cry;
//
//import dev.christopherbell.libs.api.exception.ResourceNotFoundException;
//import dev.christopherbell.libs.api.exception.InvalidTokenException;
//import dev.christopherbell.thevoid.common.VoidRequest;
//import dev.christopherbell.thevoid.common.VoidResponse;
//import dev.christopherbell.thevoid.account.model.entity.AccountEntity;
//import dev.christopherbell.thevoid.account.VoidAccountMessenger;
//import dev.christopherbell.thevoid.cry.model.Cry;
//import dev.christopherbell.thevoid.permission.PermissionsService;
//import dev.christopherbell.thevoid.utils.mappers.MapStructMapper;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Objects;
//
//@AllArgsConstructor
//@Service
//@Slf4j
//public class CryService {
//
//  private final VoidAccountMessenger voidAccountMessenger;
//  private final CryMessenger cryMessenger;
//  private final MapStructMapper mapStructMapper;
//  private final PermissionsService permissionsService;
//
//  /**
//   *
//   * @param loginToken
//   * @param accountId
//   * @param voidRequest
//   * @return
//   * @throws ResourceNotFoundException
//   * @throws InvalidTokenException
//   */
//  public VoidResponse createCry(String clientId, String loginToken, Long accountId, VoidRequest voidRequest)
//      throws ResourceNotFoundException, InvalidTokenException {
//    log.info("Request for all cries for given accountId: " + accountId);
//
//    // Validate the token for the user
//    if (!this.permissionsService.validateLoginToken(loginToken, accountId)) {
//      throw new ResourceNotFoundException();
//    }
//    var cry = Objects.requireNonNullElse(voidRequest.getCry(), new Cry());
//
//    // Convert Cry to CryEntity
//    var cryEntity = this.mapStructMapper.mapToCryEntity(cry);
//    var accountEntity = this.voidAccountMessenger.getAccountEntityById(accountId);
//    cryEntity.setAccountEntity(accountEntity);
//    this.cryMessenger.saveCryRepository(cryEntity);
//
//    return VoidResponse.builder().build();
//  }
//
//  /**
//   *
//   * @param accountId
//   * @return
//   * @throws ResourceNotFoundException
//   */
//  public VoidResponse getAllCriesByAccountId(String clientId, Long accountId) throws ResourceNotFoundException {
//    //TODO: Write a test to see if this will prevent a DB attack
//    //var username = Jsoup.clean(dirtyUsername, Safelist.basic());
//    log.info("Request for all cries for given accountId: " + accountId);
//
//    var accountEntity = this.voidAccountMessenger.getAccountEntityById(accountId);
//    var cryEntities = this.cryMessenger.findByAccountEntity(accountEntity);
//    var cries = new ArrayList<Cry>();
//    for (var cryEntity : cryEntities) {
//      var cryAuthorAccount = Objects.requireNonNullElse(cryEntity.getAccountEntity(), new AccountEntity());
//      var authorUsername = Objects.requireNonNullElse(cryAuthorAccount.getUsername(), "Anon");
//      var authorAccountId = Objects.requireNonNullElse(String.valueOf(cryAuthorAccount.getId()), "");
//      var cry = this.mapStructMapper.mapToCry(cryEntity);
//      cry.setAuthor(authorUsername);
//      cry.setAuthorAccountId(authorAccountId);
//      cries.add(cry);
//    }
//
//    return VoidResponse.builder()
//        .cries(cries)
//        .build();
//  }
//
//}
