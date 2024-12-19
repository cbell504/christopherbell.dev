package dev.christopherbell.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api.utils")
@Data
public class ApiUtilProperties {
  private String saltPassword;
}
