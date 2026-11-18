package com.cfs.RideTap.admin.controller;

import com.cfs.RideTap.admin.api.AdminDashboardApi;
import com.cfs.RideTap.admin.dto.response.AdminDashboardSummaryDTO;
import com.cfs.RideTap.admin.dto.response.RouteAnalyticsDTO;
import com.cfs.RideTap.admin.dto.response.StationTrafficDTO;
import com.cfs.RideTap.admin.service.interfaces.AdminDashboardServices;
import com.cfs.RideTap.user.dto.response.UserDTO;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class AdminDashboardController implements AdminDashboardApi {

  @Autowired
  private AdminDashboardServices adminDashboardServices;

  @Override
  @GetMapping("/summary")
  public ResponseEntity<AdminDashboardSummaryDTO> getSystemSummary() {
    return new ResponseEntity<>(adminDashboardServices.getSystemSummary(), HttpStatus.OK);
  }

  @Override
  @GetMapping("/analytics/stations")
  public ResponseEntity<List<StationTrafficDTO>> getStationTraffic() {
    return new ResponseEntity<>(adminDashboardServices.getStationTrafficAnalytics(), HttpStatus.OK);
  }

  @Override
  @GetMapping("/analytics/routes")
  public ResponseEntity<List<RouteAnalyticsDTO>> getTopRoutes(
      @RequestParam(defaultValue = "5") int limit) {
    return new ResponseEntity<>(adminDashboardServices.getTopRoutes(limit), HttpStatus.OK);
  }

  @Override
  @PutMapping("/users/{metroCardNumber}/credit")
  public ResponseEntity<UserDTO> creditUserWallet(
      @PathVariable String metroCardNumber,
      @Min(value = 1, message = "Credit amount must be at least 1") @RequestParam Double amount) {
    return new ResponseEntity<>(
        adminDashboardServices.creditUserWallet(metroCardNumber, amount),
        HttpStatus.OK
    );
  }

  @Override
  @PutMapping("/users/{metroCardNumber}/promote")
  public ResponseEntity<UserDTO> promoteUserToAdmin(@PathVariable String metroCardNumber) {
    return new ResponseEntity<>(
        adminDashboardServices.promoteToAdmin(metroCardNumber),
        HttpStatus.OK
    );
  }
}
