package com.cfs.RideTap.controllers;

import com.cfs.RideTap.apis.FareApi;
import com.cfs.RideTap.services.interfaces.FareCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/fare")
public class FareController implements FareApi {
  @Autowired
  private FareCalculator fareCalculator;

  @GetMapping
  public ResponseEntity<Double> getFare(
      @Valid @RequestParam(required = true) String originStation,
      @Valid @RequestParam(required = true) String destinationStation) {
    Double fare = fareCalculator.calculate(originStation, destinationStation);
    return new ResponseEntity<>(fare, HttpStatus.OK);
  }
}
