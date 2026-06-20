package com.cfs.RideTap.services.interfaces;

import java.util.List;

import com.cfs.RideTap.dtos.authDTO.RegisterUserRequest;
import com.cfs.RideTap.models.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;



public interface UserServices {
  public UserEntity register(RegisterUserRequest registerUserRequest);

  public UserEntity save(UserEntity userEntity);

  public List<UserEntity> findAll();

  public UserEntity findByMetroCardNumber(String metroCardNumber) throws ResponseStatusException;

  public UserEntity findByEmail(String email) throws ResponseStatusException;

  UserDetails loadUserByUsername(String email);

  public boolean existsByMetroCardNumber(String metroCardNumber);

  public void deleteByMetroCardNumber(String metroCardNumber) throws ResponseStatusException;

  public void deleteAll();
}
