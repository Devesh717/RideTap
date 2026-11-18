package com.cfs.RideTap.fare.controller;

import com.cfs.RideTap.fare.api.FareApi;
import com.cfs.RideTap.fare.service.interfaces.FareCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/fare")
@CrossOrigin(origins = "http://localhost:3000")
public class FareController implements FareApi {
  @Autowired
  private FareCalculator fareCalculator;

  @Override
  @GetMapping
  public ResponseEntity<Double> getFare(
      @Valid @RequestParam(required = true) String originStation,
      @Valid @RequestParam(required = true) String destinationStation) {
    Double fare = fareCalculator.calculate(originStation, destinationStation);
    return new ResponseEntity<>(fare, HttpStatus.OK);
  }
}
