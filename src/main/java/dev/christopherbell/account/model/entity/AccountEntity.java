package dev.christopherbell.account.model.entity;

import com.azure.data.tables.models.TableEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.christopherbell.account.model.dto.Role;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class AccountEntity {

  public static final String PARTITION_KEY = "ACCOUNT";
  public static final String PROPERTY_APPROVED_BY = "approvedBy";
  public static final String PROPERTY_CREATED_ON = "createdOn";
  public static final String PROPERTY_EMAIL = "email";
  public static final String PROPERTY_FIRST_NAME = "firstName";
  public static final String PROPERTY_INVITE_CODE = "inviteCode";
  public static final String PROPERTY_INVITE_CODE_OWNER = "inviteCodeOwner";
  public static final String PROPERTY_IS_APPROVED = "isApproved";
  public static final String PROPERTY_LAST_LOGIN_ON = "lastLoginOn";
  public static final String PROPERTY_LAST_NAME = "lastName";
  public static final String PROPERTY_LOGIN_TOKEN = "loginToken";
  public static final String PROPERTY_PASSWORD_HASH = "passwordHash";
  public static final String PROPERTY_PASSWORD_SALT = "passwordSalt";
  public static final String PROPERTY_ROLE = "role";
  public static final String PROPERTY_USERNAME = "username";

  private UUID approvedBy;
  private Instant createdOn;
  private String email;
  private String firstName;
  private UUID inviteCode;
  private UUID inviteCodeOwner;
  private Boolean isApproved;
  private Instant lastLoginOn;
  private String lastName;
  private String loginToken;
  private String passwordSalt;
  private String passwordHash;
  private Role role;
  private String rowKey;
  private String username;

  /**
   * Converts a TableEntity to an AccountEntity.
   *
   * @param entity the TableEntity to convert
   * @return the converted AccountEntity
   */
  public static AccountEntity toAccountEntity(TableEntity entity) {
    return AccountEntity.builder()
        .approvedBy((UUID) entity.getProperty(AccountEntity.PROPERTY_APPROVED_BY))
        .createdOn((Instant) entity.getProperty(AccountEntity.PROPERTY_CREATED_ON))
        .email((String) entity.getProperty(AccountEntity.PROPERTY_EMAIL))
        .firstName((String) entity.getProperty(AccountEntity.PROPERTY_FIRST_NAME))
        .inviteCode((UUID) entity.getProperty(AccountEntity.PROPERTY_INVITE_CODE))
        .inviteCodeOwner((UUID) entity.getProperty(AccountEntity.PROPERTY_INVITE_CODE_OWNER))
        .isApproved((Boolean) entity.getProperty(AccountEntity.PROPERTY_IS_APPROVED))
        .lastLoginOn((Instant) entity.getProperty(AccountEntity.PROPERTY_LAST_LOGIN_ON))
        .lastName((String) entity.getProperty(AccountEntity.PROPERTY_LAST_NAME))
        .loginToken((String) entity.getProperty(AccountEntity.PROPERTY_LOGIN_TOKEN))
        .passwordHash((String) entity.getProperty(AccountEntity.PROPERTY_PASSWORD_HASH))
        .passwordSalt((String) entity.getProperty(AccountEntity.PROPERTY_PASSWORD_SALT))
        .role(Role.valueOf((String) entity.getProperty(AccountEntity.PROPERTY_ROLE)))
        .rowKey(entity.getRowKey())
        .username((String) entity.getProperty(AccountEntity.PROPERTY_USERNAME))
        .build();
  }

  /**
   * Converts this AccountEntity to a TableEntity.
   *
   * @return a TableEntity representation of this AccountEntity
   */
  public TableEntity toTableEntity() {
    TableEntity entity = new TableEntity(PARTITION_KEY, rowKey);
    entity.addProperty(PROPERTY_APPROVED_BY, approvedBy);
    entity.addProperty(PROPERTY_CREATED_ON, createdOn);
    entity.addProperty(PROPERTY_EMAIL, email.toLowerCase());
    entity.addProperty(PROPERTY_FIRST_NAME, firstName);
    entity.addProperty(PROPERTY_INVITE_CODE, inviteCode);
    entity.addProperty(PROPERTY_INVITE_CODE_OWNER, inviteCodeOwner);
    entity.addProperty(PROPERTY_IS_APPROVED, isApproved);
    entity.addProperty(PROPERTY_LAST_LOGIN_ON, lastLoginOn);
    entity.addProperty(PROPERTY_LAST_NAME, lastName);
    entity.addProperty(PROPERTY_LOGIN_TOKEN, loginToken);
    entity.addProperty(PROPERTY_PASSWORD_HASH, passwordHash);
    entity.addProperty(PROPERTY_PASSWORD_SALT, passwordSalt);
    entity.addProperty(PROPERTY_ROLE, role);
    entity.addProperty(PROPERTY_USERNAME, username);
    return entity;
  }
}
