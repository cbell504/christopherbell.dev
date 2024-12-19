package dev.christopherbell.whatsforlunch;

import static org.mockito.Mockito.when;

import com.azure.data.tables.TableClient;
import dev.christopherbell.libs.common.api.exceptions.InvalidRequestException;
import dev.christopherbell.whatsforlunch.model.WhatsForLunchProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WhatsForLunchServiceTest {

  @InjectMocks
  private WhatsForLunchService whatsForLunchService;
  @Mock
  private WhatsForLunchProperties whatsForLunchProperties;
  @Mock
  private TableClient tableClient;

  @Test
  public void testSetRestaurantOfTheDay_success() {

    when(whatsForLunchProperties.getRestaurants()).thenReturn(WhatsForLunchStub.getRestaurantsStub());

    whatsForLunchService.setRestaurantOfTheDay();

    Assertions.assertNotNull(WhatsForLunchService.restaurantOfTheDay);
    Assertions.assertFalse(WhatsForLunchService.restaurantOfTheDay.getName().isBlank());
  }

  @Test
  public void testGetRestaurantOfTheDayTest_success() {

    when(whatsForLunchProperties.getRestaurants()).thenReturn(WhatsForLunchStub.getRestaurantsStub());

    var restaurant = whatsForLunchService.getRestaurantOfTheDay();

    Assertions.assertNotNull(WhatsForLunchService.restaurantOfTheDay);
    Assertions.assertFalse(WhatsForLunchService.restaurantOfTheDay.getName().isBlank());
    Assertions.assertEquals(restaurant.getRestaurants().getFirst().getName(),
        WhatsForLunchService.restaurantOfTheDay.getName());
  }

  @Test
  public void testGetRestaurantById_success() throws InvalidRequestException {

    when(whatsForLunchProperties.getRestaurants()).thenReturn(WhatsForLunchStub.getRestaurantsStub());

    var restaurant = whatsForLunchService.getRestaurantById(WhatsForLunchStub.RESTAURANT_ID);
    Assertions.assertEquals(restaurant.getRestaurants().getFirst().getName(),
        whatsForLunchProperties.getRestaurants().getFirst().getName());
  }

  @Test
  public void testGetRestaurantById_failure_nullId() {
    Assertions.assertThrows(InvalidRequestException.class, () -> {
      whatsForLunchService.getRestaurantById(null);
    });
  }

  @Test
  public void testGetRestaurantById_failure_blankId() {
    Assertions.assertThrows(InvalidRequestException.class, () -> {
      whatsForLunchService.getRestaurantById("");
    });
  }

  @Test
  public void testGetRestaurantById_failure_doesNotExist() {
    Assertions.assertThrows(InvalidRequestException.class, () -> {
      whatsForLunchService.getRestaurantById("-1");
    });
  }

  @Test
  public void testGetRestaurants_success() {
    var restaurants = whatsForLunchService.getRestaurants();

    Assertions.assertEquals(restaurants.getRestaurants().size(), whatsForLunchProperties.getRestaurants().size());
  }
}
