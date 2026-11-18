package com.cfs.RideTap.fare.service.interfaces;

import org.springframework.web.server.ResponseStatusException;

public interface FareCalculator {
  double calculate(String originStation, String destinationStation) throws ResponseStatusException;
}
