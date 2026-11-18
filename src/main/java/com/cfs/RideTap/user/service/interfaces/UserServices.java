package com.cfs.RideTap.user.service.interfaces;

import java.util.List;

import com.cfs.RideTap.auth.dto.request.RegisterUserRequest;
import com.cfs.RideTap.user.model.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

public interface UserServices {
  UserEntity register(RegisterUserRequest registerUserRequest);

  UserEntity save(UserEntity userEntity);

  List<UserEntity> findAll();

  UserEntity findByMetroCardNumber(String metroCardNumber) throws ResponseStatusException;

  UserEntity findByEmail(String email) throws ResponseStatusException;

  UserDetails loadUserByUsername(String email);

  boolean existsByMetroCardNumber(String metroCardNumber);

  void deleteByMetroCardNumber(String metroCardNumber) throws ResponseStatusException;

  void deleteAll();
}
