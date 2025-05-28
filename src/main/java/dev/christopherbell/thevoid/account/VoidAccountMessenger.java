//package dev.christopherbell.thevoid.account;
//
//import dev.christopherbell.libs.api.exception.ResourceNotFoundException;
//import dev.christopherbell.thevoid.account.model.entity.AccountDetailsEntity;
//import dev.christopherbell.thevoid.account.model.entity.AccountEntity;
//import dev.christopherbell.thevoid.account.model.entity.AccountSecurityEntity;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@AllArgsConstructor
//@Service
//@Slf4j
//public class VoidAccountMessenger {
//
//  private final VoidAccountRepository voidAccountRepository;
//  private final VoidAccountDetailsRepository voidAccountDetailsRepository;
//  private final VoidAccountSecurityRepository voidAccountSecurityRepository;
//
//
//  /**
//   *
//   * @return
//   */
//  public List<AccountEntity> getAccountEntities() {
//    return this.voidAccountRepository.findAll();
//  }
//
//  /**
//   *
//   * @param id
//   * @return
//   * @throws ResourceNotFoundException
//   */
//  public AccountEntity getAccountEntityById(Long id) throws ResourceNotFoundException {
//    var maybeAccountEntity = this.voidAccountRepository.findById(id);
//    if (maybeAccountEntity.isEmpty()) {
//      throw new ResourceNotFoundException();
//    }
//    return maybeAccountEntity.get();
//  }
//
//  /**
//   *
//   * @param username
//   * @return
//   * @throws ResourceNotFoundException
//   */
//  public AccountEntity getAccountEntityByUsername(String username) throws ResourceNotFoundException {
//    var maybeAccountEntity = this.voidAccountRepository.findByUsername(username);
//    if (maybeAccountEntity.isEmpty()) {
//      throw new ResourceNotFoundException();
//    }
//    return maybeAccountEntity.get();
//  }
//
//  /**
//   *
//   * @param id
//   * @return
//   * @throws ResourceNotFoundException
//   */
//  public AccountDetailsEntity getAccountDetailsEntityById(Long id) throws ResourceNotFoundException {
//    var maybeAccountDetailsEntity = this.voidAccountDetailsRepository.findById(id);
//    if (maybeAccountDetailsEntity.isEmpty()) {
//      throw new ResourceNotFoundException();
//    }
//    return maybeAccountDetailsEntity.get();
//  }
//
//  /**
//   *
//   * @param email
//   * @return
//   * @throws ResourceNotFoundException
//   */
//  public AccountSecurityEntity getAccountSecurityEntityByEmail(String email) throws ResourceNotFoundException {
//    var maybeAccountSecurityEntity = this.voidAccountSecurityRepository.findByEmail(email);
//    if (maybeAccountSecurityEntity.isEmpty()) {
//      throw new ResourceNotFoundException();
//    }
//    return maybeAccountSecurityEntity.get();
//  }
//
//  /**
//   *
//   * @param id
//   * @return
//   * @throws ResourceNotFoundException
//   */
//  public AccountSecurityEntity getAccountSecurityEntityById(Long id) throws ResourceNotFoundException {
//    var maybeAccountSecurityEntity = this.voidAccountSecurityRepository.findById(id);
//    if (maybeAccountSecurityEntity.isEmpty()) {
//      throw new ResourceNotFoundException();
//    }
//    return maybeAccountSecurityEntity.get();
//  }
//
//  /**
//   *
//   * @param accountEntity
//   */
//  public void saveAccountRepository(AccountEntity accountEntity) {
//    this.voidAccountRepository.save(accountEntity);
//  }
//
//  /**
//   *
//   * @param accountDetailsEntity
//   */
//  public void saveAccountDetailsRepository(AccountDetailsEntity accountDetailsEntity) {
//    this.voidAccountDetailsRepository.save(accountDetailsEntity);
//  }
//
//  /**
//   *
//   * @param accountSecurityEntity
//   */
//  public void saveAccountSecurityRepository(AccountSecurityEntity accountSecurityEntity) {
//    this.voidAccountSecurityRepository.save(accountSecurityEntity);
//  }
//
//  /**
//   *
//   * @param searchTerm
//   * @return
//   */
//  public List<AccountEntity> searchAccounts(String searchTerm) {
//    return this.voidAccountRepository.findByUsernameContaining(searchTerm);
//  }
//}
