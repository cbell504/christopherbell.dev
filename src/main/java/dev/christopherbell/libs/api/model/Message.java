package dev.christopherbell.libs.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a potential error or any information that will be return to the caller.
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Message {

  private String code;
  private String description;
}