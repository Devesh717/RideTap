package com.cfs.RideTap.station.repository;

import com.cfs.RideTap.station.model.Station;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
  boolean existsByNameIgnoreCase(String name);

  Optional<Station> findByNameIgnoreCaseAndCityIgnoreCase(String name, String city);

  List<Station> findByNameIgnoreCase(String name);

  List<Station> findByNameContainingIgnoreCaseOrderBySequenceNumberAsc(String query);
  List<Station> findAllByOrderBySequenceNumberAsc();

  @Query("SELECT COALESCE(MAX(s.sequenceNumber), 0) FROM Station s")
  Long findMaxSequenceNumber();
}