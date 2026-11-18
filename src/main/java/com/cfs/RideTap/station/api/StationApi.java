package com.cfs.RideTap.station.api;

import com.cfs.RideTap.station.dto.request.AddStationRequest;
import com.cfs.RideTap.station.dto.response.RouteResultDTO;
import com.cfs.RideTap.station.model.Station;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Stations & Dijkstra Routing", description = "Delhi Metro Stations, Color Lines & Shortest-Path Route Finder")
public interface StationApi {

  @Operation(summary = "Add a new Station", description = "Admin can add a new station with color line metadata")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully added a new station"),
      @ApiResponse(responseCode = "400", description = "Invalid arguments or Station already exists"),
      @ApiResponse(responseCode = "401", description = "Not Authorized")
  })
  ResponseEntity<Station> add(@Valid @RequestBody AddStationRequest addStationRequest, Authentication authentication);

  @Operation(summary = "Get all stations", description = "Fetch all Delhi Metro stations with color lines and interchange status")
  ResponseEntity<List<Station>> getAllStations();

  @Operation(summary = "Search stations by name or line", description = "Filter Delhi Metro stations by partial name and/or color line")
  ResponseEntity<List<Station>> searchStations(
      @RequestParam(required = false) String query,
      @RequestParam(required = false) String line
  );

  @Operation(summary = "Find shortest Metro Route using Dijkstra", description = "Computes optimal route, interchange stations, stop count, duration, and fare")
  ResponseEntity<RouteResultDTO> getShortestRoute(
      @RequestParam String originStation,
      @RequestParam String destinationStation
  );

  @Operation(summary = "Get Station By Id", description = "Fetch station by stationId")
  ResponseEntity<Station> getById(@Valid @PathVariable Long id);

  @Operation(summary = "Remove a station", description = "Admin can remove a station by stationId")
  ResponseEntity<?> removeById(@Valid @PathVariable Long id);

  @Operation(summary = "Remove all stations", description = "Admin can remove all stations")
  ResponseEntity<?> removeAll();
}
