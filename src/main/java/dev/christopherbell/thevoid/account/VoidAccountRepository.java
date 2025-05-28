//package dev.christopherbell.thevoid.account;
//
//import dev.christopherbell.thevoid.account.model.entity.AccountEntity;
//import java.util.List;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface VoidAccountRepository extends JpaRepository<AccountEntity, Long> {
//
//  Optional<AccountEntity> findByUsername(String username);
//  List<AccountEntity> findByUsernameContaining(String searchTerm);
//}
