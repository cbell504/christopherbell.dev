//package dev.christopherbell.thevoid.search;
//
//import dev.christopherbell.libs.api.model.Response;
//import dev.christopherbell.libs.api.exception.InvalidRequestException;
//import dev.christopherbell.libs.security.PermissionService;
//import dev.christopherbell.thevoid.common.VoidResponse;
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@AllArgsConstructor
//@RequestMapping("/api/search")
//@RestController
//public class SearchController {
//
//  public PermissionService permissionService;
//  public SearchService searchService;
//
//  //TODO: This endpoint needs some security
//
//  /**
//   *
//   * @param clientId
//   * @param searchTerm
//   * @return
//   * @throws InvalidRequestException
//   */
//  @GetMapping(value = "/{searchTerm}", produces = MediaType.APPLICATION_JSON_VALUE)
//  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
//  public ResponseEntity<Response<VoidResponse>> search(@RequestHeader String clientId,
//      @PathVariable String searchTerm) throws InvalidRequestException {
//    return new ResponseEntity<>(Response.<VoidResponse>builder()
//        .payload(searchService.search(clientId, searchTerm))
//        .success(true)
//        .build(), HttpStatus.OK);
//  }
//}
