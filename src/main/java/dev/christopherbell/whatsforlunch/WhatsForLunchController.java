package dev.christopherbell.whatsforlunch;

import dev.christopherbell.libs.common.api.models.Response;
import dev.christopherbell.libs.common.api.exceptions.InvalidRequestException;
import dev.christopherbell.permission.PermissionService;
import dev.christopherbell.whatsforlunch.model.WhatsForLunchResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Represents a controller handling all requests related to What's For Lunch
 */
@AllArgsConstructor
@RequestMapping("/api/whatsforlunch")
@RestController
public class WhatsForLunchController {

  private final PermissionService permissionService;
  private final WhatsForLunchService whatsForLunchService;


  /**
   * Takes an id and uses it to get a matching restaurant.
   *
   * @return a WhatsForLunchResponse containing the matching restaurant with the requested id.
   */
  @GetMapping(value = "/v1/restaurants/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@permissionService.hasAuthority('USER')")
  public ResponseEntity<Response<WhatsForLunchResponse>> getRestaurantById(
      HttpServletRequest request, @PathVariable String id)
      throws InvalidRequestException {
    return new ResponseEntity<>(
        Response.<WhatsForLunchResponse>builder()
            .payload(whatsForLunchService.getRestaurantById(id))
            .success(true)
            .build(), HttpStatus.OK);
  }

  /**
   * Returns all existing restaurants.
   *
   * @return a WhatsForLunchResponse containing all existing restaurants.
   */
  @GetMapping(value = "/v1/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@permissionService.hasAuthority('USER')")
  public ResponseEntity<Response<WhatsForLunchResponse>> getRestaurants(HttpServletRequest request) {
    return new ResponseEntity<>(
        Response.<WhatsForLunchResponse>builder()
            .payload(whatsForLunchService.getRestaurants())
            .success(true)
            .build(), HttpStatus.OK);
  }
}