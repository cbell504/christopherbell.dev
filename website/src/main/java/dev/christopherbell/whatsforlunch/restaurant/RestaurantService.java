package dev.christopherbell.whatsforlunch.restaurant;

import dev.christopherbell.libs.api.exception.InvalidRequestException;
import dev.christopherbell.libs.api.exception.ResourceExistsException;
import dev.christopherbell.libs.api.exception.ResourceNotFoundException;
import dev.christopherbell.whatsforlunch.restaurant.model.RestaurantCreateRequest;
import dev.christopherbell.whatsforlunch.restaurant.model.Restaurant;
import dev.christopherbell.whatsforlunch.restaurant.model.RestaurantDetail;
import dev.christopherbell.whatsforlunch.restaurant.model.RestaurantUpdateRequest;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RestaurantService {
  private final RestaurantMapper restaurantMapper;
  private final RestaurantRepository restaurantRepository;

  /**
   * Creates a new restaurant based on the provided request.
   *
   * @param request containing the details of the restaurant to be created.
   * @return a WhatsForLunchResponse containing the created restaurant details.
   * @throws Exception if there is an error during the creation process.
   */
  public RestaurantDetail createRestaurant(RestaurantCreateRequest request) throws Exception {
    var restaurant = restaurantMapper.toRestaurant(request);

    try {
      var savedRestaurant = restaurantRepository.save(restaurant);
      return restaurantMapper.toRestaurantDetail(savedRestaurant);
    } catch (DuplicateKeyException e) {
      throw new ResourceExistsException("Restaurant already exists", e);
    } catch (DataAccessException e) {
      throw new RuntimeException("Failed to save restaurant", e);
    }
  }

  /**
   * Deletes a restaurant by the provided id.
   *
   * @param id the restaurant id to delete; must not be null or blank
   * @return the details of the deleted restaurant (snapshot of its state before deletion)
   * @throws InvalidRequestException if the id is null or blank
   * @throws ResourceNotFoundException if no restaurant with the provided id exists
   * @throws RuntimeException if the deletion fails due to persistence errors
   */
  public RestaurantDetail deleteRestaurantById(
      String id
  ) throws InvalidRequestException, ResourceNotFoundException {
    if (id == null || id.isBlank()) {
      throw new InvalidRequestException("Restaurant id cannot be null or blank.");
    }

    var restaurant = restaurantRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + id));

    try {
      restaurantRepository.delete(restaurant);
    } catch (DataAccessException e) {
      throw new RuntimeException("Failed to delete restaurant with id: " + id, e);
    }
    return restaurantMapper.toRestaurantDetail(restaurant);
  }

  /**
   * Gets all existing restaurants.
   *
   * @return a WhatsForLunchResponse containing a list of all existing restaurants
   */
  public List<RestaurantDetail> getRestaurants() {
    var restaurants = Optional.of(restaurantRepository.findAll())
        .orElseGet(List::of);
    return restaurants.stream()
        .map(restaurantMapper::toRestaurantDetail)
        .toList();
  }

  /**
   * Gets a restaurant by a requested id.
   *
   * @param id of the requested restaurant.
   * @return WhatsForLunchResponse containing the requested restaurant.
   * @throws InvalidRequestException is id is null or empty, or if restaurant is not found.
   */
  public RestaurantDetail getRestaurantById(
      String id
  ) throws InvalidRequestException, ResourceNotFoundException {
    if (id == null || id.isBlank()) {
      throw new InvalidRequestException("Restaurant id cannot be null or blank.");
    }

    return restaurantRepository.findById(id)
        .map(restaurantMapper::toRestaurantDetail)
        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + id));
  }

  /**
   * Updates an existing restaurant based on the provided request.
   *
   * @param request the details of the restaurant to update; must not be null and must include a valid id
   * @return the updated restaurant details
   * @throws InvalidRequestException if the request or id is null/blank
   * @throws ResourceNotFoundException if the restaurant with the given id does not exist
   * @throws RuntimeException if persistence fails
   */
  public RestaurantDetail updateRestaurant(
      RestaurantUpdateRequest request
  ) throws ResourceNotFoundException, InvalidRequestException {
    if (request == null || request.id() == null || request.id().isBlank()) {
      throw new InvalidRequestException("Restaurant id cannot be null or blank.");
    }

    var existing = restaurantRepository.findById(request.id())
        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + request.id()));

    var restaurantToUpdate = restaurantMapper.toRestaurant(request);
    restaurantToUpdate.setId(existing.getId());

    try {
      var saved = restaurantRepository.save(restaurantToUpdate);
      return restaurantMapper.toRestaurantDetail(saved);
    } catch (DataAccessException e) {
      throw new RuntimeException("Failed to update restaurant with id: " + request.id(), e);
    }
  }

  /**
   * This is a nightly job that will select a random restaurant per day.
   */
  @Scheduled(cron = "@midnight")
  public void setRestaurantOfTheDay() {
  }

  /**
   * Generates a random number based on the size of the total number of restaurants passed in.
   *
   * @param restaurants that we store in our application.yml file
   * @return a random number between 0 and total number of restaurants in our list.
   */
  private int getRandomInt(List<Restaurant> restaurants) {
    var random = new Random();
    return random.nextInt(restaurants.size());
  }
}
