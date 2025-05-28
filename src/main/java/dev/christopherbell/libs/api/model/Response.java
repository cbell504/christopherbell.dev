package dev.christopherbell.libs.api.model;

import java.util.UUID;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * This class represents a base level response class with common fields that every response should contain.
 */
@Data
@SuperBuilder(toBuilder = true)
public class Response<T> {

  private List<Message> messages;
  private T payload;
  private UUID requestId;
  private boolean success;
}