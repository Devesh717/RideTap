package com.cfs.RideTap.repositories;

import com.cfs.RideTap.models.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
  public boolean existsByName(String name);
  public Optional<Station> findByName(String name);
  Station findTopByOrderBySequenceNumberDesc();
  @Query("SELECT COALESCE(MAX(s.sequenceNumber), 0) FROM Station s")
  Long findMaxSequenceNumber();
}
