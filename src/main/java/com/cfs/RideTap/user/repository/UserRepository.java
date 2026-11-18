package com.cfs.RideTap.user.repository;

import java.util.Optional;

import com.cfs.RideTap.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByMetroCardNumber(String metroCardNumber);

  Optional<UserEntity> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByMobileNumber(String mobileNumber);

  boolean existsByMetroCardNumber(String metroCardNumber);

  void deleteByMetroCardNumber(String metroCardNumber);
}
