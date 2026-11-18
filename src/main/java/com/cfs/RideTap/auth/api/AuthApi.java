package com.cfs.RideTap.auth.api;

import com.cfs.RideTap.auth.dto.request.LoginRequest;
import com.cfs.RideTap.auth.dto.request.RegisterUserRequest;
import com.cfs.RideTap.auth.dto.response.LoginResponse;
import com.cfs.RideTap.user.dto.response.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "User Authentication", description = "User Authentication Creation and Login")
public interface AuthApi {
  @Operation(summary = "User Registration", description = "Register user with new unique Metro Card Number")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully registered the user"),
      @ApiResponse(responseCode = "400", description = "Bad request")
  })
  ResponseEntity<UserDTO> register(@RequestBody RegisterUserRequest registerUserRequest);

  @Operation(summary = "User Login", description = "Login user and create session")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully logged in"),
      @ApiResponse(responseCode = "400", description = "Bad request")
  })
  ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request);
}
