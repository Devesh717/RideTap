package com.cfs.RideTap.admin.api;

import com.cfs.RideTap.admin.dto.response.AdminDashboardSummaryDTO;
import com.cfs.RideTap.admin.dto.response.RouteAnalyticsDTO;
import com.cfs.RideTap.admin.dto.response.StationTrafficDTO;
import com.cfs.RideTap.user.dto.response.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Admin Operations & Analytics", description = "Dedicated Admin Console KPIs, Wallet Adjustments, and Transit Analytics")
public interface AdminDashboardApi {

  @Operation(
      summary = "Get System KPI Summary",
      description = "Returns total users, active/cancelled bookings, total confirmed revenue, wallet liability, and station count"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved system summary"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - Admin role required")
  })
  ResponseEntity<AdminDashboardSummaryDTO> getSystemSummary();

  @Operation(
      summary = "Get Station Traffic Analytics",
      description = "Returns boarding (origin) and alighting (destination) ride counts per station ordered by route sequence"
  )
  ResponseEntity<List<StationTrafficDTO>> getStationTraffic();

  @Operation(
      summary = "Get Top Popular Routes",
      description = "Returns the most frequently booked origin-to-destination corridors along with confirmed revenue"
  )
  ResponseEntity<List<RouteAnalyticsDTO>> getTopRoutes(@RequestParam(defaultValue = "5") int limit);

  @Operation(
      summary = "Admin Credit User Wallet",
      description = "Allows an administrator to credit promotional or refund funds directly to a commuter's Metro Card"
  )
  ResponseEntity<UserDTO> creditUserWallet(
      @PathVariable String metroCardNumber,
      @Min(value = 1, message = "Credit amount must be at least 1") @RequestParam Double amount
  );

  @Operation(
      summary = "Promote User to Admin",
      description = "Grants ROLE_ADMIN privileges to an existing user account by Metro Card Number"
  )
  ResponseEntity<UserDTO> promoteUserToAdmin(@PathVariable String metroCardNumber);
}
