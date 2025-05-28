package dev.christopherbell.libs.api.model;

public enum ErrorCode {
  INVALID_REQUEST("001", "Invalid request"),
  ACCOUNT_NOT_FOUND("002", "Account not found"),
  ACCOUNT_USER_NAME_EXISTS("003", "Account username already exists"),
  INVALID_TOKEN("004", "Invalid token"),
  TOO_MANY_REQUESTS("005", "Too many requests"),
  GENERIC_ERROR("999", "An unexpected error occurred");

  private final String code;
  private final String defaultMessage;

  ErrorCode(String code, String defaultMessage) {
    this.code = code;
    this.defaultMessage = defaultMessage;
  }

  public String getCode() {
    return code;
  }

  public String getDefaultMessage() {
    return defaultMessage;
  }
}
