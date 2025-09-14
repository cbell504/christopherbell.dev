package dev.christopherbell.libs.api.model;

import java.util.UUID;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Generic API response envelope containing a payload and metadata.
 *
 * @param <T> payload type
 */
@Data
@SuperBuilder(toBuilder = true)
public class Response<T> {

  private List<Message> messages;
  private T payload;
  private UUID requestId;
  private boolean success;
}
