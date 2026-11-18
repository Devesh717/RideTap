package com.cfs.RideTap.user.service.implementations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.cfs.RideTap.auth.dto.request.RegisterUserRequest;
import com.cfs.RideTap.user.enums.Role;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.repository.UserRepository;
import com.cfs.RideTap.user.service.interfaces.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
      throw new IllegalArgumentException("User entity cannot be null");
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
    userEntity.setRoles(new ArrayList<>(List.of(Role.USER)));

    userEntity.setMetroCardNumber(
            "MC" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase()
    );

    return userRepository.save(userEntity);
  }

  @Override
  public List<UserEntity> findAll() {
    return userRepository.findAll();
  }

  @Override
  public UserEntity findByMetroCardNumber(String metroCardNumber) throws ResponseStatusException {
    return userRepository.findByMetroCardNumber(metroCardNumber)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by provided metro card number"));
  }

  @Override
  public UserEntity findByEmail(String email) throws ResponseStatusException {
    return userRepository.findByEmail(email)
            .orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by provided email id")
            );
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
  @Transactional
  public void deleteByMetroCardNumber(String metroCardNumber) throws ResponseStatusException {
    if (!userRepository.existsByMetroCardNumber(metroCardNumber)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by provided metro card number");
    }
    userRepository.deleteByMetroCardNumber(metroCardNumber);
  }

  @Override
  @Transactional
  public void deleteAll() {
    userRepository.deleteAll();
  }
}
