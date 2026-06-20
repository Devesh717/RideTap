package com.cfs.RideTap.services.implementations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.cfs.RideTap.dtos.authDTO.RegisterUserRequest;
import com.cfs.RideTap.enums.Role;
import com.cfs.RideTap.models.UserEntity;
import com.cfs.RideTap.repositories.UserRepository;
import com.cfs.RideTap.services.interfaces.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class UserServicesImpl implements UserServices {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public UserEntity save(UserEntity userEntity) throws IllegalArgumentException {
    if (userEntity == null) {
      throw new IllegalArgumentException();
    }

    return userRepository.save(userEntity);
  }

  @Override
  public UserEntity register(RegisterUserRequest registerUserRequest) {
    UserEntity userEntity = new UserEntity();
    LocalDateTime dateTime = LocalDateTime.now();

    userEntity.setHolderName(registerUserRequest.getHolderName());
    userEntity.setEmail(registerUserRequest.getEmail());
    userEntity.setMobileNumber(registerUserRequest.getMobileNumber());
    userEntity.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
    userEntity.setCreatedAt(dateTime);
    userEntity.setBalance(1000);
    userEntity.setBookings(new ArrayList<>());

    if (registerUserRequest.isAdmin()) {
      userEntity.setRoles(Collections.singleton(Role.ADMIN));
    } else {
      userEntity.setRoles(Collections.singleton(Role.USER));
    }

    userEntity.setMetroCardNumber(
            "MC" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase()
    );

    UserEntity savedUser = userRepository.save(userEntity);

    return savedUser;
  }

  @Override
  public List<UserEntity> findAll() {
    return userRepository.findAll();
  }

  @Override
  public UserEntity findByMetroCardNumber(String metroCardNumber) throws ResponseStatusException {
    UserEntity userEntity = userRepository.findByMetroCardNumber(metroCardNumber)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by provided metro card number"));

    return userEntity;
  }

  @Override
  public UserEntity findByEmail(String email) throws ResponseStatusException {
    UserEntity userEntity = userRepository.findByEmail(email)
            .orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by provided email id")
            );

    return userEntity;
  }

  @Override
  public UserDetails loadUserByUsername(String email) {
    UserEntity user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                    new UsernameNotFoundException("User not found"));

    return org.springframework.security.core.userdetails.User
            .builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .authorities(
                    user.getRoles()
                            .stream()
                            .map(role -> "ROLE_" + role.name())
                            .toArray(String[]::new)
            )
            .build();
  }

  @Override
  public boolean existsByMetroCardNumber(String metroCardNumber) {
    return userRepository.existsByMetroCardNumber(metroCardNumber);
  }

  @Override
  public void deleteByMetroCardNumber(String metroCardNumber) throws ResponseStatusException {
    if (!userRepository.existsByMetroCardNumber(metroCardNumber)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by provided metro card number");
    }

    userRepository.deleteByMetroCardNumber(metroCardNumber);
  }

  @Override
  public void deleteAll() {
    userRepository.deleteAll();
  }
}
