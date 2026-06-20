package com.cfs.RideTap.services.interfaces;

import org.springframework.web.server.ResponseStatusException;

public interface FareCalculator {
  public double calculate(String originStation, String destinationStation) throws ResponseStatusException;
}
