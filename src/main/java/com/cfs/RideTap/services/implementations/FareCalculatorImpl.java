package com.cfs.RideTap.services.implementations;

import com.cfs.RideTap.models.Station;
import com.cfs.RideTap.services.interfaces.FareCalculator;
import com.cfs.RideTap.services.interfaces.StationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class FareCalculatorImpl implements FareCalculator {
  private final int FARE_RATE = 5; // 5 rs

  @Autowired
  private StationServices stationServices;

  @Override
  public double calculate(String originStation,
                          String destinationStation) {

    Station origin = stationServices.findByName(originStation)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Origin station not found"));

    Station destination = stationServices.findByName(destinationStation)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Destination station not found"));

    long stationsTravelled = Math.abs(
            origin.getSequenceNumber()
                    - destination.getSequenceNumber());

    return Math.max(stationsTravelled * 2.0, 2.0);
  }

}
