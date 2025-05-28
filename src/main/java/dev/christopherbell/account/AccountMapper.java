package dev.christopherbell.account;

import dev.christopherbell.account.model.dto.Account;
import dev.christopherbell.account.model.entity.AccountEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

  Account toAccount(AccountEntity accountEntity);
}
