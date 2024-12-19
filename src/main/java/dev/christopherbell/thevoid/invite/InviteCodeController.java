package dev.christopherbell.thevoid.invite;

import dev.christopherbell.libs.common.api.models.Response;
import dev.christopherbell.libs.common.api.exceptions.ResourceNotFoundException;
import dev.christopherbell.libs.common.api.exceptions.InvalidRequestException;
import dev.christopherbell.libs.common.api.exceptions.InvalidTokenException;
import dev.christopherbell.permission.PermissionService;
import dev.christopherbell.thevoid.common.VoidRequest;
import dev.christopherbell.thevoid.common.VoidResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/api/invite/code")
@RestController
public class InviteCodeController {

  public final PermissionService permissionService;
  public final InviteCodeService inviteCodeService;

  @PostMapping(value = "/v1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@permissionService.hasAuthority('ADMIN')")
  public ResponseEntity<Response<VoidResponse>> generateInviteCode(@RequestHeader String clientId,
      @RequestHeader String loginToken,
      @RequestBody VoidRequest voidRequest)
      throws InvalidRequestException, ResourceNotFoundException, InvalidTokenException {
    return new ResponseEntity<>(Response.<VoidResponse>builder()
        .payload(inviteCodeService.generateInviteCode(clientId, loginToken, voidRequest))
        .success(true)
        .build(), HttpStatus.CREATED);
  }
}
