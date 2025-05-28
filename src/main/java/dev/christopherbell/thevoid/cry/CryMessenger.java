//package dev.christopherbell.thevoid.cry;
//
//import dev.christopherbell.thevoid.account.model.entity.AccountEntity;
//import dev.christopherbell.thevoid.cry.model.CryEntity;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@AllArgsConstructor
//@Service
//@Slf4j
//public class CryMessenger {
//
//  private final CryRepository cryRepository;
//
//  /**
//   *
//   * @param accountEntity
//   * @return
//   */
//  public List<CryEntity> findByAccountEntity(AccountEntity accountEntity) {
//    return this.cryRepository.findByAccountEntity(accountEntity);
//  }
//
//  /**
//   *
//   * @param cryEntity
//   */
//  public void saveCryRepository(CryEntity cryEntity) {
//    this.cryRepository.save(cryEntity);
//  }
//}
