package com.cfs.RideTap.auth.service.interfaces;

import com.cfs.RideTap.auth.dto.request.LoginRequest;
import com.cfs.RideTap.auth.dto.request.RegisterUserRequest;
import com.cfs.RideTap.auth.dto.response.LoginResponse;
import com.cfs.RideTap.auth.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterUserRequest request);

    LoginResponse login(LoginRequest request);

    void logout(String token);
}
