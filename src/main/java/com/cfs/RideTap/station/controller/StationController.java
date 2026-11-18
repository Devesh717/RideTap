package com.cfs.RideTap.station.controller;

import com.cfs.RideTap.station.api.StationApi;
import com.cfs.RideTap.station.dto.request.AddStationRequest;
import com.cfs.RideTap.station.dto.response.RouteResultDTO;
import com.cfs.RideTap.station.model.Station;
import com.cfs.RideTap.station.service.interfaces.StationServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stations")
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class StationController implements StationApi {
  @Autowired
  private StationServices stationServices;

  @Override
  @PostMapping
  public ResponseEntity<Station> add(@Valid @RequestBody AddStationRequest addStationRequest, Authentication authentication) {
    Station savedStation = stationServices.add(addStationRequest);
    return new ResponseEntity<>(savedStation, HttpStatus.CREATED);
  }

  @Override
  @GetMapping
  public ResponseEntity<List<Station>> getAllStations() {
    List<Station> stations = stationServices.findAll();
    return new ResponseEntity<>(stations, HttpStatus.OK);
  }

  @Override
  @GetMapping("/search")
  public ResponseEntity<List<Station>> searchStations(
      @RequestParam(required = false) String query,
      @RequestParam(required = false) String line) {
    return new ResponseEntity<>(stationServices.searchStations(query, line), HttpStatus.OK);
  }

  @Override
  @GetMapping("/route")
  public ResponseEntity<RouteResultDTO> getShortestRoute(
      @RequestParam String originStation,
      @RequestParam String destinationStation) {
    return new ResponseEntity<>(
        stationServices.findShortestRoute(originStation, destinationStation),
        HttpStatus.OK
    );
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<Station> getById(@Valid @PathVariable Long id) {
    Station station = stationServices.findById(id);
    return new ResponseEntity<>(station, HttpStatus.OK);
  }

  @Override
  @DeleteMapping("/{id}")
  public ResponseEntity<?> removeById(@Valid @PathVariable Long id) {
    stationServices.deleteById(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Override
  @DeleteMapping
  public ResponseEntity<?> removeAll() {
    stationServices.deleteAll();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
