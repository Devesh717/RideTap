package com.cfs.RideTap.services.implementations;

import java.util.List;
import java.util.Optional;

import com.cfs.RideTap.dtos.stationDTO.AddStationRequest;
import com.cfs.RideTap.models.Station;
import com.cfs.RideTap.repositories.StationRepository;
import com.cfs.RideTap.services.interfaces.StationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class StationServicesImpl implements StationServices {
  @Autowired
  private StationRepository stationRepository;

  @Override
  public Station add(AddStationRequest addStationRequest)
          throws ResponseStatusException {

    if (stationRepository.existsByName(addStationRequest.getName())) {
      throw new ResponseStatusException(
              HttpStatus.CONFLICT,
              "Station already exists with this name");
    }

    Long maxSequence = stationRepository.findMaxSequenceNumber();

    Station station = new Station();
    station.setName(addStationRequest.getName());
    station.setSequenceNumber(maxSequence + 1);

    return stationRepository.save(station);
  }

  @Override
  public List<Station> findAll() {
    return stationRepository.findAll();
  }

  @Override
  public boolean existsById(Long id) {
    return stationRepository.existsById(id);
  }

  @Override
  public Station findById(Long id) throws ResponseStatusException {
    return stationRepository
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found by provided id"));
  }

  @Override
  public Optional<Station> findByName(String name) {
    return stationRepository.findByName(name);
  }

  @Override
  public void deleteById(Long id) throws ResponseStatusException {
    if (!stationRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found by provided id");
    }

    stationRepository.deleteById(id);
  }

  @Override
  public void deleteAll() {
    stationRepository.deleteAll();
  }

}
