package com.cfs.RideTap.fare.service.implementations;

import com.cfs.RideTap.fare.service.interfaces.FareCalculator;
import com.cfs.RideTap.station.dto.response.RouteResultDTO;
import com.cfs.RideTap.station.service.interfaces.StationServices;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class FareCalculatorImpl implements FareCalculator {

  @Autowired
  private StationServices stationServices;

  @Override
  public double calculate(String originStation, String destinationStation) {
    RouteResultDTO route = stationServices.findShortestRoute(originStation, destinationStation);
    return route.getFare();
  }
}
