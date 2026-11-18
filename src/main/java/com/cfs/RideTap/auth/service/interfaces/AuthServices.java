package com.cfs.RideTap.auth.service.interfaces;

import com.cfs.RideTap.auth.dto.request.LoginRequest;
import com.cfs.RideTap.auth.dto.request.RegisterUserRequest;
import com.cfs.RideTap.auth.dto.response.LoginResponse;
import com.cfs.RideTap.user.dto.response.UserDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthServices {
  UserDTO register(RegisterUserRequest registerRequest);
  LoginResponse login(LoginRequest loginRequest, HttpServletRequest request);
}
