package dev.christopherbell.account;

import com.azure.data.tables.models.TableEntity;
import dev.christopherbell.account.models.AccountEntity;
import dev.christopherbell.account.models.Role;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TableEntityUtils {

  public static AccountEntity toAccountEntity(TableEntity entity) {

    var approvedBy = (UUID) entity.getProperty(AccountEntity.PROPERTY_APPROVED_BY);
    var createdOn = (OffsetDateTime) entity.getProperty(AccountEntity.PROPERTY_CREATED_ON);
    var email = (String) entity.getProperty(AccountEntity.PROPERTY_EMAIL);
    var firstName = (String) entity.getProperty(AccountEntity.PROPERTY_FIRST_NAME);
    var isApproved = (Boolean) entity.getProperty(AccountEntity.PROPERTY_IS_APPROVED);
    var lastName = (String) entity.getProperty(AccountEntity.PROPERTY_LAST_NAME);
    var passwordHash = (String) entity.getProperty(AccountEntity.PROPERTY_PASSWORD_HASH);
    var passwordSalt =  (String) entity.getProperty(AccountEntity.PROPERTY_PASSWORD_SALT);
    var role = Role.valueOf((String) entity.getProperty(AccountEntity.PROPERTY_ROLE));
    var username = (String) entity.getProperty(AccountEntity.PROPERTY_USERNAME);

    return AccountEntity.builder()
        .approvedBy(approvedBy)
        .createdOn(createdOn.toInstant())
        .email(email)
        .firstName(firstName)
        .isApproved(isApproved)
        .lastName(lastName)
        .role(role)
        .rowKey(email)
        .passwordHash(passwordHash)
        .passwordSalt(passwordSalt)
        .username(username)
        .build();
  }

}
