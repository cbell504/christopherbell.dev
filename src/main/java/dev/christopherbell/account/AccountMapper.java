package dev.christopherbell.account;

import dev.christopherbell.account.models.Account;
import dev.christopherbell.account.models.AccountEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

  AccountEntity toAccountEntity(Account account);

  Account toAccount(AccountEntity accountEntity);
}
