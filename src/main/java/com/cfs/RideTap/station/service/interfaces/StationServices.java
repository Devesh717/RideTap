package com.cfs.RideTap.station.service.interfaces;

import com.cfs.RideTap.station.dto.request.AddStationRequest;
import com.cfs.RideTap.station.dto.response.RouteResultDTO;
import com.cfs.RideTap.station.model.Station;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

public interface StationServices {
  Station add(AddStationRequest addStationRequest) throws ResponseStatusException;

  List<Station> findAll();

  List<Station> searchStations(String query, String line);

  RouteResultDTO findShortestRoute(String originName, String destinationName) throws ResponseStatusException;

  boolean existsById(Long id);

  Station findById(Long id) throws ResponseStatusException;

  Optional<Station> findByName(String name);

  void deleteById(Long id) throws ResponseStatusException;

  void deleteAll();
}
