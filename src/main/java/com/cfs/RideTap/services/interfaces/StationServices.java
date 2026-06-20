package com.cfs.RideTap.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.cfs.RideTap.dtos.stationDTO.AddStationRequest;
import com.cfs.RideTap.models.Station;
import org.springframework.web.server.ResponseStatusException;



public interface StationServices {
  public Station add(AddStationRequest addStationRequest) throws ResponseStatusException;

  public List<Station> findAll();

  public boolean existsById(Long id);

  public Station findById(Long id) throws ResponseStatusException;

  public Optional<Station> findByName(String name) throws ResponseStatusException;

  public void deleteById(Long id) throws ResponseStatusException;

  public void deleteAll();
}
