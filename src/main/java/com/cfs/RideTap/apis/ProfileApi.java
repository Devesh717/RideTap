package com.cfs.RideTap.apis;

import com.cfs.RideTap.dtos.userDTO.UserDTO;
import org.springframework.http.ResponseEntity;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "User Profile", description = "Get User Profile")
public interface ProfileApi {
  @Operation(summary = "Get User Profile")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully fetched user profile"),
  })
  public ResponseEntity<UserDTO> getProfile(HttpServletRequest request);
}
