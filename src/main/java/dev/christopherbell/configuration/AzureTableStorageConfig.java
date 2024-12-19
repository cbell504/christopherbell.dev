package dev.christopherbell.configuration;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AzureTableStorageConfig {

  @Value("${azure.storage.connection-string}")
  private String connectionString;
  @Value("${azure.storage.table-name}")
  private String tableName;

  @Bean
  public TableClient tableClient() {
    return new TableClientBuilder()
        .connectionString(connectionString)
        .tableName(tableName)
        .buildClient();
  }
}
