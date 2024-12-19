package dev.christopherbell.photo;

import dev.christopherbell.libs.common.api.models.Response;
import dev.christopherbell.photo.model.PhotoResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Represents the controller that handles all photo gallery related requests.
 */
@AllArgsConstructor
@RequestMapping("/api/photo")
@RestController
public class PhotoController {

  private final PhotoService photoService;

  /**
   * Returns all existing images for the photo gallery.
   *
   * @return a PhotoGalleryResponse containing all existing images.
   */
  @GetMapping(value = "/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@permissionService.hasAuthority('USER')")
  public ResponseEntity<Response<PhotoResponse>> getImages() {
    return new ResponseEntity<>(
        Response.<PhotoResponse>builder()
            .payload(photoService.getAllImages())
            .success(true)
            .build(), HttpStatus.OK);
  }
}