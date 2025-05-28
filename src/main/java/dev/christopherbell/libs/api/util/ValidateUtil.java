package dev.christopherbell.libs.api.util;

import dev.christopherbell.libs.api.exception.InvalidRequestException;
import dev.christopherbell.thevoid.common.VoidRequest;
import dev.christopherbell.thevoid.account.model.dto.Account;
import dev.christopherbell.thevoid.account.model.dto.AccountSecurity;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ValidateUtil {

  private ValidateUtil() {}

  public static final ArrayList<String> ACCEPTED_CLIENT_IDs = new ArrayList<>(
      List.of("void-client")
  );

  public static String getCleanEmailAddress(AccountSecurity accountSecurity) {
    var dirtyEmail = Objects.requireNonNullElse(accountSecurity.getEmail(), APIConstants.EMPTY_STRING);
    return Jsoup.clean(dirtyEmail, Safelist.basic());
  }

  public static String getCleanPassword(AccountSecurity accountSecurity) {
    var dirtyPassword = Objects.requireNonNullElse(accountSecurity.getPassword(), APIConstants.EMPTY_STRING);
    return Jsoup.clean(dirtyPassword, Safelist.basic());
  }

  public static String getCleanUsername(Account account) {
    var dirtyUsername = Objects.requireNonNullElse(account.getUsername(), APIConstants.EMPTY_STRING);
    return Jsoup.clean(dirtyUsername, Safelist.basic());
  }

  public static void validateAccount(VoidRequest voidRequest) throws InvalidRequestException {
    if (Objects.isNull(voidRequest)) {
      throw new InvalidRequestException("The request is null");
    }
    if (Objects.isNull(voidRequest.getAccount())) {
      throw new InvalidRequestException("The request contains no account information");
    }
  }
}