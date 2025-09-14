package dev.christopherbell.photo.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Builder
@Configuration
@ConfigurationProperties(prefix = "photo-properties")
@Data
public class PhotoProperties {

  private final List<Photo> photos;
}
