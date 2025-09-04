package dev.christopherbell.account;

import dev.christopherbell.account.model.Account;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Account} entities in MongoDB.
 *
 * <p>
 * This interface extends {@link MongoRepository}, providing CRUD operations
 * and custom query methods for the {@link Account} entity. It leverages
 * Spring Data's repository abstraction to simplify data access and manipulation.
 * </p>
 *
 * <p>
 * The primary key type for {@link Account} is {@link UUID}.
 * </p>
 *
 * <p>
 * Custom query methods can be defined by following Spring Data's method
 * naming conventions, allowing for automatic implementation of common
 * queries without the need for boilerplate code.
 * </p>
 *
 * @see MongoRepository
 * @see Account
 */
@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

  /**
   * Retrieves an {@link Account} by its unique email address.
   *
   * <p>
   * This query method leverages Spring Data's derived query generation.
   * If an {@code AccountEntity} with the given email exists, it will be
   * returned wrapped in an {@link Optional}; otherwise, the result will
   * be {@link Optional#empty()}.
   * </p>
   *
   * @param email the email address to look up (must not be {@code null})
   * @return an {@link Optional} containing the matching {@link Account}
   *         if found, or {@link Optional#empty()} if no match exists
   */
  Optional<Account> findByEmail(String email);

  /**
   * Finds an {@link Account} by its unique username.
   *
   * <p>
   * This query method is automatically implemented by Spring Data based
   * on the method name. If a record with the specified username exists,
   * it will be returned wrapped in an {@link Optional}; otherwise, the
   * result will be {@link Optional#empty()}.
   * </p>
   *
   * @param username the username to search for (must not be {@code null})
   * @return an {@link Optional} containing the matching {@link Account}
   *         if found, or {@link Optional#empty()} if no match exists
   */
  Optional<Account> findByUsername(String username);
}
