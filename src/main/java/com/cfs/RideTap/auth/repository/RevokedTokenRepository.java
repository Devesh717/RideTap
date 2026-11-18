package com.cfs.RideTap.auth.repository;

import com.cfs.RideTap.auth.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {

    boolean existsByTokenHashAndExpiryDateAfter(String tokenHash, LocalDateTime now);
}
