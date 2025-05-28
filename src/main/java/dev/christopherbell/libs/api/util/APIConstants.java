package dev.christopherbell.libs.api.util;

/**
 * Represents a spot to keep commonly used constants.
 */
public final class APIConstants {

  private APIConstants() {
  }

  public static final String EMPTY_STRING = "";
  public static final String FAILURE = "failure";
  public static final String SUCCESS = "success";

  public static final String VALIDATION_BAD_CLIENT_ID = "The client's ID is not valid.";
  public static final String VALIDATION_BAD_EMAIL = "The given email is not valid.";
  public static final String VALIDATION_BAD_PASSWORD = "The given password is not valid.";
  public static final String VALIDATION_BAD_USERNAME = "The given username is not valid.";
  public static final String VALIDATION_NULL_ACCOUNT_INFO = "The request contains no account information.";
  public static final String VALIDATION_NULL_REQUEST = "The request is null.";
}