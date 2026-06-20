package com.cfs.RideTap.repositories;

import java.util.Optional;

import com.cfs.RideTap.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByMetroCardNumber(String metroCardNumber);

  Optional<UserEntity> findByEmail(String email);

  boolean existsByMetroCardNumber(String metroCardNumber);

  void deleteByMetroCardNumber(String metroCardNumber);
}
