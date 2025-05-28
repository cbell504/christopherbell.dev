package dev.christopherbell.whatsforlunch;

import com.azure.data.tables.TableClient;
import dev.christopherbell.libs.api.exception.InvalidRequestException;
import dev.christopherbell.whatsforlunch.model.Restaurant;
import dev.christopherbell.whatsforlunch.model.WhatsForLunchProperties;
import dev.christopherbell.whatsforlunch.model.WhatsForLunchResponse;
import java.util.List;
import java.util.Objects;

import java.util.Random;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class WhatsForLunchService {

  private final TableClient tableClient;
  private final WhatsForLunchProperties whatsForLunchProperties;
  public static Restaurant restaurantOfTheDay;
  public static int randomNumberForRestaurant;

  /**
   * Gets the restaurant of the day.
   *
   * @return a WhatsForLunchResponse containing the restaurant of the day.
   */
  public WhatsForLunchResponse getRestaurantOfTheDay() {
    if(Objects.isNull(restaurantOfTheDay)
        || restaurantOfTheDay.getName().isBlank()) {
      setRestaurantOfTheDay();
    }

    return WhatsForLunchResponse.builder()
        .restaurants(List.of(restaurantOfTheDay))
        .build();
  }

  /**
   * This is a nightly job that will select a random restaurant per day.
   */
  @Scheduled(cron = "@midnight")
  public void setRestaurantOfTheDay() {
    var restaurants = whatsForLunchProperties.getRestaurants();
    var randomNumber = 0;
    do {
      randomNumber = getRandomInt(restaurants);
    } while (randomNumberForRestaurant == randomNumber);

    randomNumberForRestaurant = randomNumber;
    restaurantOfTheDay = restaurants.get(randomNumberForRestaurant);
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

  /**
   * Gets a restaurant by a requested id.
   *
   * @param id of the requested restaurant.
   * @return WhatsForLunchResponse containing the requested restaurant.
   * @throws InvalidRequestException is id is null or empty, or if restaurant is not found.
   */
  public WhatsForLunchResponse getRestaurantById(String id) throws InvalidRequestException {
    if (Objects.isNull(id) || id.isBlank()) {
      throw new InvalidRequestException("Id cannot be null or blank.");
    }

    for (var restaurant : whatsForLunchProperties.getRestaurants()) {
      if (restaurant.getId().equals(UUID.fromString(id))) {
        return WhatsForLunchResponse.builder()
            .restaurants(List.of(restaurant))
            .build();
      }
    }

    throw new InvalidRequestException("No restaurant found");
  }

  /**
   * Gets all existing restaurants.
   *
   * @return a WhatsForLunchResponse containing a list of all existing restaurants
   */
  public WhatsForLunchResponse getRestaurants() {
    return WhatsForLunchResponse.builder()
        .restaurants(whatsForLunchProperties.getRestaurants())
        .build();
  }
}